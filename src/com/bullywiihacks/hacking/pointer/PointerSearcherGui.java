package com.bullywiihacks.hacking.pointer;

import javax.swing.*;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import com.bullywiihacks.hacking.pointer.memory.MemoryDump;
import com.bullywiihacks.hacking.pointer.search.PointerSearch;
import com.bullywiihacks.hacking.pointer.search.WiiUPointerSearch;
import com.bullywiihacks.hacking.pointer.utilities.files.BinaryFilesReader;
import com.bullywiihacks.hacking.pointer.utilities.files.SimpleProperties;
import com.bullywiihacks.hacking.pointer.utilities.swing.CustomDialog;
import com.bullywiihacks.hacking.pointer.utilities.swing.MessageConsole;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.Toolkit;

/**
 * A graphical interface for the pointer search application
 */
@SuppressWarnings("serial")
public class PointerSearcherGui extends JFrame
{
	private String programName = "Wii U Pointer Search";
	private String programVersion = "2.7";
	private String programAuthor = "Bully@WiiPlaza";

	private PointerSearch pointerSearch;

	private SimpleProperties simpleProperties;

	private JTextArea outputTextArea;
	private JButton searchButton;
	private JButton readButton;
	private JCheckBoxMenuItem storeToFileOption;

	private List<MemoryDump> memoryDumps;

	public PointerSearcherGui() throws IOException, InterruptedException
	{
		simpleProperties = new SimpleProperties();
		setFrameProperties();
		setMenuBar();
		setFrameLayout();
		setReadMemoryDumpsButton();
		setSearchButton();
		setResultsArea();
	}

	private void setSearchButton() throws IOException
	{
		String searchButtonText = "Perform Search";
		searchButton = new JButton(searchButtonText);
		searchButton
				.setToolTipText("Performs a pointer search if at least a memory dump source directory is given");
		searchButton.addActionListener(searchPerformed ->
		{
			searchButton.setEnabled(false);
			searchButton.setText("Searching...");
			outputTextArea.setText("");

			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground()
				{
					try
					{
						String storedMaximumPointerOffsetString = simpleProperties
								.get(OptionKeys.MAXIMUM_OFFSET);
						String storedAllowNegativeOffsetsString = simpleProperties
								.get(OptionKeys.NEGATIVE_OFFSETS);

						pointerSearch = new WiiUPointerSearch(memoryDumps);

						if (storedMaximumPointerOffsetString != null)
						{
							int storedMaximumPointerOffset = Integer
									.parseInt(
											storedMaximumPointerOffsetString,
											16);
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

						String storedPointerInPointerValue = simpleProperties.get(
								OptionKeys.POINTER_IN_POINTER);

						if (storedPointerInPointerValue == null || storedPointerInPointerValue.equals("false"))
						{
							System.out.println("Performing pointer search...");

							pointerSearch
									.performPointerSearch(storeToFileOption
											.isSelected());
						} else
						{
							System.out.println("Performing pointer in pointer search...");

							pointerSearch
									.performPointerInPointerSearch(storeToFileOption
											.isSelected());
						}

						System.out.print("Search completed!");
					} catch (Exception e)
					{
						e.printStackTrace();
					}

					return null;
				}

				@Override
				public void done()
				{
					searchButton.setEnabled(true);
					searchButton.setText(searchButtonText);
				}
			}.execute();
		});

		GridBagConstraints gbc_performSearchButton = new GridBagConstraints();
		gbc_performSearchButton.insets = new Insets(0, 0, 5, 0);
		gbc_performSearchButton.gridx = 0;
		gbc_performSearchButton.gridy = 1;
		getContentPane().add(searchButton, gbc_performSearchButton);
		setSearchButtonAvailability();
	}

	private void setReadMemoryDumpsButton()
	{
		readButton = new JButton("Read memory dumps");

		readButton.addActionListener(clickEvent ->
		{
			outputTextArea.setText("");
			readButton.setEnabled(false);
			String folder = simpleProperties
					.get(OptionKeys.MEMORY_DUMPS_FOLDER);

			System.out.print("Reading memory dump files into RAM... ");

			new SwingWorker<String, String>()
			{
				@Override
				protected String doInBackground() throws Exception
				{
					try
					{
						memoryDumps = BinaryFilesReader
								.readMemoryDumps(folder);

						searchButton.setEnabled(true);
						System.out.println("OK!");
					} catch (IOException ioException)
					{
						ioException.printStackTrace();
					} finally
					{
						readButton.setEnabled(true);
					}

					return null;
				}
			}.execute();
		});

		GridBagConstraints gbc_readMemoryDumps = new GridBagConstraints();
		gbc_readMemoryDumps.insets = new Insets(0, 0, 5, 0);
		gbc_readMemoryDumps.gridx = 0;
		gbc_readMemoryDumps.gridy = 0;
		getContentPane().add(readButton, gbc_readMemoryDumps);
	}

	private void setResultsArea()
	{
		JLabel resultsLabel = new JLabel("Results:");
		GridBagConstraints gbc_resultsLabel = new GridBagConstraints();
		gbc_resultsLabel.insets = new Insets(0, 0, 5, 0);
		gbc_resultsLabel.gridx = 0;
		gbc_resultsLabel.gridy = 2;
		getContentPane().add(resultsLabel, gbc_resultsLabel);

		outputTextArea = new JTextArea();
		outputTextArea.setToolTipText("Results will be displayed here");
		outputTextArea.setEditable(false);
		GridBagConstraints gbc_resultsArea = new GridBagConstraints();
		gbc_resultsArea.fill = GridBagConstraints.BOTH;
		gbc_resultsArea.gridx = 0;
		gbc_resultsArea.gridy = 3;
		getContentPane().add(new JScrollPane(outputTextArea), gbc_resultsArea);

		MessageConsole messageConsole = new MessageConsole(outputTextArea);
		messageConsole.redirectOut();
		messageConsole.redirectErr(Color.RED, null);
	}

	private void setFrameLayout()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
	}

	private void setMenuBar() throws IOException
	{
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu optionsMenu = new JMenu("Options");
		menuBar.add(optionsMenu);

		addMemoryDumpsFolderOption(optionsMenu);
		addAllowNegativeOffsetsOption(optionsMenu);
		addMaximumPointerOffsetOption(optionsMenu);
		addPointerInPointerOption(optionsMenu);
		addStoreToFileOption(optionsMenu);
	}

	private void addMemoryDumpsFolderOption(JMenu optionsMenu)
	{
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

				browseFolderButton.addActionListener(browsingFolder ->
				{
					JFileChooser fileChooser = new JFileChooser();
					fileChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					String currentFolderPath = folderPathField.getText();
					File currentFolder = new File(currentFolderPath);

					if (!currentFolder.exists())
					{
						fileChooser.setCurrentDirectory(IOUtilities
								.getWorkingDirectory());
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
				});

				memoryDumpsDialog.addComponent(browseFolderButton);
				memoryDumpsDialog.addComponent(folderPathField);
				int selection = memoryDumpsDialog.show();
				if (selection == JOptionPane.OK_OPTION)
				{
					String folderPath = folderPathField.getText();

					if (!IOUtilities.folderExists(folderPath))
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
					} else
					{
						simpleProperties.put(OptionKeys.MEMORY_DUMPS_FOLDER,
								folderPathField.getText());
					}
				}
			}
		});

		optionsMenu.add(memoryDumpsFolderOption);
	}

	private void addAllowNegativeOffsetsOption(JMenu optionsMenu)
	{
		JCheckBoxMenuItem allowNegativeOffsetsCheckBox = new JCheckBoxMenuItem(
				"Allow negative offsets");
		allowNegativeOffsetsCheckBox.addActionListener(clickedEvent ->
		{
			boolean isSelected = allowNegativeOffsetsCheckBox.isSelected();

			simpleProperties.put(OptionKeys.NEGATIVE_OFFSETS,
					String.valueOf(isSelected));
		});
		optionsMenu.add(allowNegativeOffsetsCheckBox);

		boolean isSelected = Boolean.parseBoolean(simpleProperties
				.get(OptionKeys.NEGATIVE_OFFSETS));

		allowNegativeOffsetsCheckBox.setSelected(isSelected);
	}

	private void addMaximumPointerOffsetOption(JMenu optionsMenu)
	{
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
	}

	private void addPointerInPointerOption(JMenu optionsMenu)
	{
		JCheckBoxMenuItem pointerInPointerSearchCheckBox = new JCheckBoxMenuItem(
				"Pointer in pointer search");

		pointerInPointerSearchCheckBox.addActionListener(clickedEvent ->
		{
			boolean isSelected = pointerInPointerSearchCheckBox
					.isSelected();

			simpleProperties.put(OptionKeys.POINTER_IN_POINTER,
					String.valueOf(isSelected));
		});

		boolean pointerInPointerSelected = Boolean
				.parseBoolean(simpleProperties
						.get(OptionKeys.POINTER_IN_POINTER));

		pointerInPointerSearchCheckBox.setSelected(pointerInPointerSelected);

		optionsMenu.add(pointerInPointerSearchCheckBox);
	}

	private void addStoreToFileOption(JMenu optionsMenu)
	{
		storeToFileOption = new JCheckBoxMenuItem("Store to file");
		storeToFileOption.addActionListener(clickedEvent ->
		{
			boolean isSelected = storeToFileOption.isSelected();

			simpleProperties.put(OptionKeys.STORE_TO_FILE,
					String.valueOf(isSelected));
		});

		boolean storeToFileOptionSelected = Boolean
				.parseBoolean(simpleProperties.get(OptionKeys.STORE_TO_FILE));

		storeToFileOption.setSelected(storeToFileOptionSelected);

		optionsMenu.add(storeToFileOption);
	}

	private void setFrameProperties()
	{
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle(programName + " v" + programVersion + " by " + programAuthor);
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						PointerSearcherGui.class
								.getResource("/com/bullywiihacks/hacking/pointer/images/Wii U.png")));
	}

	private void setSearchButtonAvailability()
	{
		searchButton.setEnabled(memoryDumps != null);
	}
}