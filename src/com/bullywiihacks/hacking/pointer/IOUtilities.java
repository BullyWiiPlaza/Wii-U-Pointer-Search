package com.bullywiihacks.hacking.pointer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

	public static void append(String fileName, String text) throws IOException
	{
		if(!new File(fileName).exists())
		{
			new File(fileName).createNewFile();
		}

		Files.write(Paths.get(fileName), text.concat(System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
	}

	public static void delete(String fileName)
	{
		File file = new File(fileName);

		if(file.exists())
		{
			file.delete();
		}
	}
}