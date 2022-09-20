package com.bullywiihacks.hacking.pointer.search;

import java.util.ArrayList;
import java.util.List;

import com.bullywiihacks.hacking.pointer.HexadecimalNumber;
import com.bullywiihacks.hacking.pointer.memory.MemoryDump;

/**
 * A class for representing memory pointers
 */
public class MemoryPointer
{
	private int baseOffset;
	private int startingOffset;
	private List<Integer> pointerOffsets;

	public static final int INVALID_POINTER = -1;

	public MemoryPointer(int baseOffset, int startingOffset)
	{
		this.baseOffset = baseOffset;
		setStartingOffset(startingOffset);
		pointerOffsets = new ArrayList<>();
	}

	public void setStartingOffset(int startingOffset)
	{
		this.startingOffset = startingOffset;
	}

	public void addPointerOffset(int pointerOffset)
	{
		pointerOffsets.add(pointerOffset);
	}

	/**
	 * @param memoryDump
	 *            The memory dump to use
	 * @return The memory address the pointer is pointing to or {@value #INVALID_POINTER} if invalid
	 */
	public int followPointer(MemoryDump memoryDump)
	{
		int pointerValue;
		int pointerDestination = baseOffset;
		int pointerOffsetsCount = pointerOffsets.size();

		for (int pointerOffsetsIndex = 0; pointerOffsetsIndex < pointerOffsetsCount; pointerOffsetsIndex++)
		{
			if(!memoryDump.getMemoryBounds().isValidMemoryAddress(pointerDestination + startingOffset))
			{
				return INVALID_POINTER;
			}

			pointerValue = memoryDump.readValueAt(pointerDestination);
			pointerDestination = pointerValue
					+ pointerOffsets.get(pointerOffsetsIndex);

			if (pointerOffsetsIndex != pointerOffsetsCount - 1)
			{
				pointerDestination -= startingOffset;
			}
		}

		return pointerDestination;
	}

	/**
	 * @param memoryDump The memory dump to use for verifying the pointer's functionality
	 * @return True if the pointer reaches the target address false otherwise
	 */
	public boolean reachesTargetAddress(MemoryDump memoryDump)
	{
		int destinationAddress = followPointer(memoryDump);
		int targetAddress = memoryDump.getTargetAddress();

		return destinationAddress == targetAddress;
	}

	/**
	 * @param memoryDumps
	 *            The memory dumps to verify the pointer on
	 * @param startingIndex The list index to start at
	 * @return True if the memory pointer works on all additional memory dumps false
	 *         otherwise
	 */
	public boolean reachesTargetAddresses(List<MemoryDump> memoryDumps)
	{
		for (int memoryDumpsIndex = 0; memoryDumpsIndex < memoryDumps.size(); memoryDumpsIndex++)
		{
			if (!reachesTargetAddress(memoryDumps.get(memoryDumpsIndex)))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 *
	 * @param memoryDump The memory dump to use
	 * @return The difference between the pointer value and the target address
	 */
	public int getPointerOffset(MemoryDump memoryDump)
	{
		int readValue = memoryDump.readValueAt(baseOffset);

		if (!memoryDump.getMemoryBounds().isValidMemoryAddress(readValue))
		{
			return INVALID_POINTER;
		}

		return memoryDump.getTargetAddress() - readValue;
	}

	/**
	 * @return The hexadecimal notation of the pointer
	 */
	@Override
	public String toString()
	{
		StringBuilder pointerRepresentationBuilder = new StringBuilder();

		for (int openingBracketsIndex = 0; openingBracketsIndex < pointerOffsets
				.size(); openingBracketsIndex++)
		{
			pointerRepresentationBuilder.append("[");
		}

		pointerRepresentationBuilder.append(new HexadecimalNumber(baseOffset
				+ startingOffset));

		for (Integer pointerOffset1 : pointerOffsets)
		{
			HexadecimalNumber pointerOffset = new HexadecimalNumber(
					pointerOffset1);

			pointerRepresentationBuilder.append("] ");

			if (!pointerOffset.isNegative())
			{
				pointerRepresentationBuilder.append("+ ");
			}

			pointerRepresentationBuilder.append(pointerOffset);
		}

		return pointerRepresentationBuilder.toString();
	}
}