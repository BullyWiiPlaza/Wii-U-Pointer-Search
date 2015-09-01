package de.bullywiiplaza.hacking.pointer.utilities;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class BinaryFilesDetector
{
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