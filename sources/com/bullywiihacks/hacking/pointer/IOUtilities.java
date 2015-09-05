package com.bullywiihacks.hacking.pointer;

import java.io.File;

/**
 * Utility methods for verifying the correctness of user input
 */
public class IOUtilities
{
	public static boolean binaryFilesFolderExists(String folder)
	{
		return new File(folder).exists();
	}

	public static boolean isHexadecimal(String text)
	{
		return text.matches("^[0-9a-fA-F]+$");
	}

	public static File getWorkingDirectory()
	{
		return new File(System.getProperty("user.dir"));
	}

	public static boolean isDivisibleBy4(int number)
	{
		return number % 4 == 0;
	}
}