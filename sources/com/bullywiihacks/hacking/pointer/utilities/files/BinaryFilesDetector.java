package com.bullywiihacks.hacking.pointer.utilities.files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class BinaryFilesDetector
{
	/**
	 * @param folderPath The folder to analyze
	 * @return A list containing all binary files in the current folder
	 */
	public static List<File> getBinaryFiles(String folderPath)
	{
		File[] files = new File(folderPath).listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File currentFile, String currentFilename)
			{
				return currentFilename.toLowerCase().endsWith(".bin");
			}
		});

		return Arrays.asList(files);
	}
}