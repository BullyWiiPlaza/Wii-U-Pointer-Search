package com.bullywiihacks.hacking.pointer.memory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.bullywiihacks.hacking.pointer.search.MemoryPointer;

/**
 * A representation of a memory dump including helpful methods for pointer searches
 */
public class MemoryDump
{
	private ByteBuffer memory;
	private int targetAddress;
	private MemoryBounds memoryBounds;

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

	public int readPointerValue(int offset)
	{
		int readValue = readValueAt(offset);

		if(!memoryBounds.isValidMemoryAddress(readValue))
		{
			return MemoryPointer.INVALID_POINTER;
		}
		else
		{
			return readValue;
		}
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

	public MemoryBounds getMemoryBounds()
	{
		return memoryBounds;
	}

	public void setMemoryBounds(MemoryBounds memoryBounds)
	{
		this.memoryBounds = memoryBounds;
	}
}