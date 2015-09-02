package com.bullywiihacks.hacking.pointer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.UIManager;

/**
 * Useful methods for running the application with certain settings or VM
 * arguments
 */
public class ApplicationLaunchingUtilities
{
	public static String getJarName() throws FileNotFoundException
	{
		Class<PointerSearcherGui> theClass = PointerSearcherGui.class;

		String path = theClass.getResource(theClass.getSimpleName() + ".class")
				.getFile();
		if (path.startsWith("/"))
		{
			throw new FileNotFoundException("This is not a jar file: \n" + path);
		}

		path = ClassLoader.getSystemClassLoader().getResource(path).getFile();

		return new File(path.substring(0, path.lastIndexOf('!'))).getName()
				.replaceAll("%20", " ");
	}

	public static void runApplication() throws Exception
	{
		setSystemLookAndFeel();

		new PointerSearcherGui().setVisible(true);
	}

	private static void setSystemLookAndFeel() throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}

	public static void relaunchWithMoreMemory() throws IOException
	{
		Runtime runtime = Runtime.getRuntime();
		String jarName;

		try
		{
			jarName = getJarName();

			int grantedGigaBytes = 10;

			if (runtime.maxMemory() < 5 * Math.pow(10, 9))
			{
				runtime.exec("java -Xmx" + grantedGigaBytes + "G -jar \"" + jarName + "\"");

				System.exit(0);
			}
		} catch (FileNotFoundException noJar)
		{

		}
	}
}