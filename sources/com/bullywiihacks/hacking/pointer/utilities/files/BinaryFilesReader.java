package com.bullywiihacks.hacking.pointer.utilities.files;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bullywiihacks.hacking.pointer.memory.MemoryDump;

public class BinaryFilesReader
{
	/**
	 * @param folderPath The folder to analyze
	 * @return A list containing all binary files in the current folder
	 */
	private static List<File> getBinaryFiles(String folderPath)
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

	/**
	 * @param folderPath The folder to analyze
	 * @return A list containing all memory dump objects in the current folder
	 */
	public static List<MemoryDump> readMemoryDumps(String memoryDumpsFolder) throws IOException
	{
		List<File> binaryFiles = getBinaryFiles(memoryDumpsFolder);
		List<MemoryDump> memoryDumps = new ArrayList<>();

		for (File binaryFile : binaryFiles)
		{
			MemoryDump memoryDump = new MemoryDump(binaryFile.getAbsolutePath());

			memoryDumps.add(memoryDump);
		}

		return memoryDumps;
	}
}