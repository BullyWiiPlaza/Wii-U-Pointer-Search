package de.bullywiiplaza.hacking.pointer.utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

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

	public void put(String key, String value) throws IOException
	{
		outputStream = new FileOutputStream(configurationFileName);

		properties.setProperty(key, value);

		properties.store(outputStream, null);
	}

	public String get(String key) throws IOException
	{
		if (!new File(configurationFileName).exists())
		{
			return null;
		}

		inputStream = new FileInputStream(configurationFileName);

		properties.load(inputStream);

		return properties.getProperty(key);
	}
}