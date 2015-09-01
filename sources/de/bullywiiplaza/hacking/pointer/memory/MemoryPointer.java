package de.bullywiiplaza.hacking.pointer.memory;

public class MemoryPointer
{
	private int startingOffset;
	private int highestMemoryAddress;

	public MemoryPointer(int startingOffset, int fileSize)
	{
		this.startingOffset = startingOffset;
		highestMemoryAddress = startingOffset + fileSize - 1;
	}

	public boolean isValidPointer(int value)
	{
		return value >= startingOffset && value <= highestMemoryAddress;
	}

	public int getStartingOffset()
	{
		return startingOffset;
	}
}