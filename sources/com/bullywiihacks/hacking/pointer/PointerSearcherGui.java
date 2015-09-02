package com.bullywiihacks.hacking.pointer;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.bullywiihacks.hacking.pointer.search.PointerSearch;
import com.bullywiihacks.hacking.pointer.search.WiiUPointerSearch;
import com.bullywiihacks.hacking.pointer.utilities.files.SimpleProperties;
import com.bullywiihacks.hacking.pointer.utilities.swing.CustomDialog;
import com.bullywiihacks.hacking.pointer.utilities.swing.MessageConsole;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Toolkit;

/**
 * A graphical interface for the pointer search application
 */
@SuppressWarnings("serial")
public class PointerSearcherGui extends JFrame
{
	private String programName = "Wii U Pointer Search";
	private String programVersion = "2.3";
	private String programAuthor = "Bully@WiiPlaza";

	private SimpleProperties simpleProperties;

	private JTextArea resultsArea;
	private JButton searchButton;

	public PointerSearcherGui() throws IOException
	{
		simpleProperties = new SimpleProperties();

		setFrameProperties();

		setMenuBar();

		setFrameLayout();

		setSearchButton();

		setResultsArea();
	}

	private void setSearchButton() throws IOException
	{
		searchButton = new JButton("Perform Search");
		searchButton
				.setToolTipText("Performs a pointer search if at least a memory dump source directory is given");
		searchButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent searchPerformed)
			{
				searchButton.setEnabled(false);
				resultsArea.setText("");

				new SwingWorker<String, String>()
				{
					@Override
					protected String doInBackground() throws Exception
					{
						String storedMaximumPointerOffsetString = simpleProperties
								.get(OptionKeys.MAXIMUM_OFFSET);
						String storedAllowNegativeOffsetsString = simpleProperties
								.get(OptionKeys.NEGATIVE_OFFSETS);

						PointerSearch pointerSearch = new WiiUPointerSearch(
								simpleProperties
										.get(OptionKeys.MEMORY_DUMPS_FOLDER));

						if (storedMaximumPointerOffsetString != null)
						{
							int storedMaximumPointerOffset = Integer.parseInt(
									storedMaximumPointerOffsetString, 16);
							pointerSearch
									.setMaximumPointerOffset(storedMaximumPointerOffset);
						}

						if (storedAllowNegativeOffsetsString != null)
						{
							boolean storedAllowNegativeOffsets = Boolean
									.parseBoolean(storedAllowNegativeOffsetsString);
							pointerSearch
									.setAllowNegativeOffsets(storedAllowNegativeOffsets);
						}

						if(simpleProperties.get(OptionKeys.POINTER_IN_POINTER).equals("true"))
						{
							pointerSearch.performPointerInPointerSearch();
						}
						else
						{
							pointerSearch.performPointerSearch();
						}

						System.out.print("Search completed!");

						return null;
					}

					@Override
					public void done()
					{
						searchButton.setEnabled(true);
					}
				}.execute();
			}
		});

		GridBagConstraints gbc_performSearchButton = new GridBagConstraints();
		gbc_performSearchButton.insets = new Insets(0, 0, 5, 0);
		gbc_performSearchButton.gridx = 0;
		gbc_performSearchButton.gridy = 0;
		getContentPane().add(searchButton, gbc_performSearchButton);
		setSearchButtonAvailability();
	}

	private void setResultsArea()
	{
		JLabel resultsLabel = new JLabel("Results:");
		GridBagConstraints gbc_resultsLabel = new GridBagConstraints();
		gbc_resultsLabel.insets = new Insets(0, 0, 5, 0);
		gbc_resultsLabel.gridx = 0;
		gbc_resultsLabel.gridy = 1;
		getContentPane().add(resultsLabel, gbc_resultsLabel);

		resultsArea = new JTextArea();
		resultsArea.setToolTipText("Results will be displayed here");
		resultsArea.setEditable(false);
		GridBagConstraints gbc_resultsArea = new GridBagConstraints();
		gbc_resultsArea.fill = GridBagConstraints.BOTH;
		gbc_resultsArea.gridx = 0;
		gbc_resultsArea.gridy = 2;
		getContentPane().add(new JScrollPane(resultsArea), gbc_resultsArea);

		MessageConsole messageConsole = new MessageConsole(resultsArea);
		messageConsole.redirectOut();
		messageConsole.redirectErr(Color.RED, null);
	}

	private void setFrameLayout()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
	}

	private void setMenuBar() throws IOException
	{
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu optionsMenu = new JMenu("Options");
		menuBar.add(optionsMenu);

		JMenuItem memoryDumpsFolderOption = new JMenuItem(
				"Memory dumps folder...");
		memoryDumpsFolderOption.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent memoryDumpSelection)
			{
				CustomDialog memoryDumpsDialog = new CustomDialog();
				memoryDumpsDialog.setRootPane(getRootPane());
				memoryDumpsDialog
						.setOptions(new String[] { "Save", "Discard" });
				memoryDumpsDialog.setTitle("Please select a folder!");
				JButton browseFolderButton = new JButton("Browse...");
				JTextField folderPathField = new JTextField();

				String storedMemoryDumpsFolder = simpleProperties
						.get(OptionKeys.MEMORY_DUMPS_FOLDER);

				if (storedMemoryDumpsFolder == null)
				{
					folderPathField.setText(IOUtilities.getWorkingDirectory()
							.getAbsolutePath());
				} else
				{
					folderPathField.setText(storedMemoryDumpsFolder);
				}

				browseFolderButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent browsingFolder)
					{
						JFileChooser fileChooser = new JFileChooser();
						fileChooser
								.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						String currentFolderPath = folderPathField.getText();
						File currentFolder = new File(currentFolderPath);

						if (!currentFolder.exists())
						{
							fileChooser
									.setCurrentDirectory(IOUtilities.getWorkingDirectory());
						} else
						{
							fileChooser.setCurrentDirectory(currentFolder);
						}

						int selection = fileChooser
								.showOpenDialog(getRootPane());

						if (selection == JFileChooser.APPROVE_OPTION)
						{
							folderPathField.setText(fileChooser
									.getSelectedFile().getAbsolutePath());
						}
					}
				});

				memoryDumpsDialog.addComponent(browseFolderButton);
				memoryDumpsDialog.addComponent(folderPathField);
				int selection = memoryDumpsDialog.show();
				if (selection == JOptionPane.OK_OPTION)
				{
					String folderPath = folderPathField.getText();

					if (!IOUtilities.binaryFilesFolderExists(folderPath))
					{
						CustomDialog folderNotExistsDialog = new CustomDialog();
						folderNotExistsDialog.setTitle("Error");
						folderNotExistsDialog
								.addMessageText("Folder does not exist!");
						folderNotExistsDialog
								.setMessageType(JOptionPane.ERROR_MESSAGE);
						folderNotExistsDialog.setOptions(new String[] { "OK" });
						folderNotExistsDialog.show();

						actionPerformed(memoryDumpSelection);
					} else if (!IOUtilities.enoughBinaryFiles(folderPath))
					{
						CustomDialog insufficientBinaryFiles = new CustomDialog();
						insufficientBinaryFiles
								.setMessageType(JOptionPane.ERROR_MESSAGE);
						insufficientBinaryFiles.setTitle("Error");
						insufficientBinaryFiles
								.setOptions(new String[] { "OK" });
						insufficientBinaryFiles
								.addMessageText("The given folder has to contain at least one memory dump (\".bin\" file)!");
						insufficientBinaryFiles.show();

						actionPerformed(memoryDumpSelection);
					} else
					{
						simpleProperties.put(
								OptionKeys.MEMORY_DUMPS_FOLDER,
								folderPathField.getText());
						setSearchButtonAvailability();
					}
				}
			}
		});
		optionsMenu.add(memoryDumpsFolderOption);

		JCheckBoxMenuItem allowNegativeOffsetsCheckBox = new JCheckBoxMenuItem(
				"Allow negative offsets");
		allowNegativeOffsetsCheckBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent clickedEvent)
			{
				boolean isSelected = allowNegativeOffsetsCheckBox.isSelected();

				simpleProperties.put(OptionKeys.NEGATIVE_OFFSETS,
						String.valueOf(isSelected));
				setSearchButtonAvailability();
			}
		});
		optionsMenu.add(allowNegativeOffsetsCheckBox);

		boolean isSelected = Boolean.parseBoolean(simpleProperties
				.get(OptionKeys.NEGATIVE_OFFSETS));

		allowNegativeOffsetsCheckBox.setSelected(isSelected);

		JMenuItem maximumPointerOffsetButton = new JMenuItem(
				"Maximum pointer offset...");
		maximumPointerOffsetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent clickedEvent)
			{
				CustomDialog maximumPointerOffsetDialog = new CustomDialog();
				maximumPointerOffsetDialog.setTitle("Please enter!");
				maximumPointerOffsetDialog
						.addMessageText("Maximum pointer offset (in hexadecimal)?");
				maximumPointerOffsetDialog.setRootPane(getRootPane());
				maximumPointerOffsetDialog
						.setMessageType(JOptionPane.QUESTION_MESSAGE);
				maximumPointerOffsetDialog.setOptions(new String[] { "Save",
						"Discard" });
				JTextField maximumPointerOffsetField = new JTextField();

				String storedMaximumPointerOffset = simpleProperties
						.get(OptionKeys.MAXIMUM_OFFSET);

				if (storedMaximumPointerOffset == null)
				{
					maximumPointerOffsetField.setText(""
							+ PointerSearch.DEFAULT_MAXIMUM_POINTER_OFFSET);
				} else
				{
					maximumPointerOffsetField
							.setText(storedMaximumPointerOffset);
				}

				maximumPointerOffsetDialog
						.addComponent(maximumPointerOffsetField);
				int selection = maximumPointerOffsetDialog.show();
				if (selection == JOptionPane.YES_OPTION)
				{
					String maximumPointerOffsetContents = maximumPointerOffsetField
							.getText();

					if (IOUtilities.isHexadecimal(maximumPointerOffsetContents))
					{
						simpleProperties.put(OptionKeys.MAXIMUM_OFFSET,
								maximumPointerOffsetField.getText());
						setSearchButtonAvailability();

					} else
					{
						CustomDialog noHexadecimalDialog = new CustomDialog();
						noHexadecimalDialog.setTitle("Error");
						noHexadecimalDialog
								.addMessageText("The given offset is no valid hexadecimal!");
						noHexadecimalDialog
								.setMessageType(JOptionPane.ERROR_MESSAGE);
						noHexadecimalDialog.setOptions(new String[] { "OK" });
						noHexadecimalDialog.show();

						actionPerformed(clickedEvent);
					}
				}
			}
		});

		optionsMenu.add(maximumPointerOffsetButton);

		JCheckBoxMenuItem pointerInPointerSearchCheckBox = new JCheckBoxMenuItem("Pointer in pointer search");

		pointerInPointerSearchCheckBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent clickedEvent)
			{
				boolean isSelected = pointerInPointerSearchCheckBox.isSelected();

				simpleProperties.put(OptionKeys.POINTER_IN_POINTER,
						String.valueOf(isSelected));
				setSearchButtonAvailability();
			}
		});

		boolean pointerInPointerSelected = Boolean.parseBoolean(simpleProperties
				.get(OptionKeys.POINTER_IN_POINTER));

		pointerInPointerSearchCheckBox.setSelected(pointerInPointerSelected);

		optionsMenu.add(pointerInPointerSearchCheckBox);
	}

	private void setFrameProperties()
	{
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(programName + " v" + programVersion + " by " + programAuthor);
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						PointerSearcherGui.class
								.getResource("/com/bullywiihacks/hacking/pointer/images/Wii U.png")));
	}

	private void setSearchButtonAvailability()
	{
		String storedTargetPath = simpleProperties
				.get(OptionKeys.MEMORY_DUMPS_FOLDER);

		if (storedTargetPath != null)
		{
			searchButton.setEnabled(true);
		} else
		{
			searchButton.setEnabled(false);
		}
	}

	public static void main(String[] arguments) throws Exception
	{
		ApplicationLaunchingUtilities.relaunchWithMoreMemory();

		ApplicationLaunchingUtilities.runApplication();
	}
}