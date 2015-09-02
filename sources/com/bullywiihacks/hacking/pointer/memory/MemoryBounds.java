package com.bullywiihacks.hacking.pointer.memory;

/**
 * A class implementing the memory bounds of a memory dump
 */
public class MemoryBounds
{
	private int startingOffset;
	private int lastMemoryOffset;

	public MemoryBounds(int startingOffset, int memoryDumpSize)
	{
		this.startingOffset = startingOffset;
		lastMemoryOffset = startingOffset + memoryDumpSize - 1;
	}

	public boolean isValidMemoryAddress(int value)
	{
		return value >= startingOffset && value <= lastMemoryOffset;
	}

	public int getStartingOffset()
	{
		return startingOffset;
	}
}