package com.bullywiihacks.hacking.pointer;

import java.io.File;
import java.util.List;

import com.bullywiihacks.hacking.pointer.utilities.files.BinaryFilesDetector;

/**
 * Utility methods for verifying the correctness of user input
 */
public class IOUtilities
{
	public static boolean binaryFilesFolderExists(String folder)
	{
		return new File(folder).exists();
	}

	public static boolean enoughBinaryFiles(String folder)
	{
		List<File> binaryFiles = BinaryFilesDetector.getBinaryFiles(folder);

		return binaryFiles.size() >= 1;
	}

	public static boolean isHexadecimal(String text)
	{
		return text.matches("^[0-9a-fA-F]+$");
	}

	public static File getWorkingDirectory()
	{
		return new File(System.getProperty("user.dir"));
	}
}
