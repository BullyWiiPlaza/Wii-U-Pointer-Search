package com.bullywiihacks.hacking.pointer;

import java.io.File;

/**
 * Utility methods for verifying the correctness of user input
 */
public class IOUtilities
{
	public static boolean folderExists(String folderPath)
	{
		return new File(folderPath).exists();
	}

	/**
	 * @param text The text to check
	 * @return True if the input is valid hexadecimal false otherwise
	 */
	public static boolean isHexadecimal(String text)
	{
		return text.matches("^[0-9a-fA-F]+$");
	}

	/**
	 * @return A File object representing the directory where the program has been started from
	 */
	public static File getWorkingDirectory()
	{
		return new File(System.getProperty("user.dir"));
	}
}