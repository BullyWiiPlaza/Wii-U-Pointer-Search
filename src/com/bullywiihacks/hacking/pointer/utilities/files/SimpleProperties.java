package com.bullywiihacks.hacking.pointer.utilities.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A class implementing a mechanism of storing key/value pairs in a local file for persistent options
 */
public class SimpleProperties
{
	private Properties properties;
	private OutputStream outputStream;
	private InputStream inputStream;
	String configurationFileName;

	public SimpleProperties() throws IOException
	{
		properties = new Properties();
		configurationFileName = "configuration.properties";
	}

	public void put(String key, String value)
	{
		try
		{
			outputStream = new FileOutputStream(configurationFileName);

			properties.setProperty(key, value);

			properties.store(outputStream, null);
		}catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
	}

	/**
	 * @param key The key to use for looking up a value
	 * @return The value corresponding to the key
	 */
	public String get(String key)
	{
		if (!new File(configurationFileName).exists())
		{
			return null;
		}

		try
		{
			inputStream = new FileInputStream(configurationFileName);

			properties.load(inputStream);
		} catch (IOException ioException)
		{
			ioException.printStackTrace();
		}

		return properties.getProperty(key);
	}
}