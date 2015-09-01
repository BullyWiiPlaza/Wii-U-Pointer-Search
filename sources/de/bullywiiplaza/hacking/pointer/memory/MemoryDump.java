package de.bullywiiplaza.hacking.pointer.memory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class MemoryDump
{
	private ByteBuffer memory;
	private int targetAddress;
	private MemoryPointer memoryPointer;

	public MemoryDump(String filePath) throws IOException
	{
		readMemoryDump(filePath);

		setTargetAddress(FilenameUtils.getBaseName(filePath));
	}

	private void setTargetAddress(String hexadecimalAddress)
	{
		targetAddress = Integer.parseInt(hexadecimalAddress, 16);
	}

	private void readMemoryDump(String filePath) throws IOException
	{
		byte[] valueBytes = FileUtils.readFileToByteArray(new File(filePath));
		memory = ByteBuffer.wrap(valueBytes);
	}

	public int readValueAt(int offset)
	{
		return memory.getInt(offset);
	}

	public int getTargetAddress()
	{
		return targetAddress;
	}

	public int getSize()
	{
		// http://stackoverflow.com/a/23148879/3764804
		return memory.capacity();
	}

	public MemoryPointer getMemoryPointer()
	{
		return memoryPointer;
	}

	public void setMemoryPointer(MemoryPointer memoryPointer)
	{
		this.memoryPointer = memoryPointer;
	}
}