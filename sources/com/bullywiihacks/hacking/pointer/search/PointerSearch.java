package com.bullywiihacks.hacking.pointer.search;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bullywiihacks.hacking.pointer.HexadecimalNumber;
import com.bullywiihacks.hacking.pointer.IOUtilities;
import com.bullywiihacks.hacking.pointer.memory.MemoryDump;
import com.bullywiihacks.hacking.pointer.memory.MemoryBounds;

/**
 * A class for performing pointer searches on memory dumps
 */
public abstract class PointerSearch
{
	private int maximumPointerOffset;
	private boolean allowNegativeOffsets;
	private int startingOffset;
	private List<MemoryDump> memoryDumps;

	public static final int DEFAULT_MAXIMUM_POINTER_OFFSET = 0x1000;
	public static final boolean DEFAULT_ALLOW_NEGATIVE_OFFSETS = false;

	public static final int INVALID_POINTER = -1;

	public PointerSearch(List<MemoryDump> memoryDumps, int startingOffset)
			throws IOException
	{
		maximumPointerOffset = DEFAULT_MAXIMUM_POINTER_OFFSET;
		allowNegativeOffsets = DEFAULT_ALLOW_NEGATIVE_OFFSETS;
		this.startingOffset = startingOffset;
		this.memoryDumps = memoryDumps;

		for (MemoryDump memoryDump : memoryDumps)
		{
			MemoryBounds memoryBounds = new MemoryBounds(startingOffset,
					memoryDump.getSize());
			memoryDump.setMemoryBounds(memoryBounds);
		}
	}

	public void performPointerSearch()
	{
		System.out.println("Performing pointer search...");

		MemoryDump memoryDump = memoryDumps.get(0);

		// Iterate over every file offset in the memory dump
		for (int currentOffset = 0; currentOffset < memoryDump.getSize(); currentOffset += 4)
		{
			// Read its value
			int readValue = memoryDump.readPointerValue(currentOffset);

			// Move on to the next offset if no valid pointer has been read
			if (readValue == INVALID_POINTER)
			{
				continue;
			}

			int pointerValue = readValue;

			// Calculate the distance between the target address and the pointer
			// value
			int pointerOffset = memoryDump.getTargetAddress() - pointerValue;

			// Make sure constraints given by the user are held
			if (!isAllowed(pointerOffset))
			{
				continue;
			}

			MemoryPointer memoryPointer = new MemoryPointer(currentOffset,
					startingOffset);
			memoryPointer.addPointerOffset(pointerOffset);

			// Verify the pointer on all given memory dumps
			if (memoryPointer.reachesTargetAddresses(memoryDumps))
			{
				System.out.println(memoryPointer);
			}
		}
	}

	public void performPointerInPointerSearch()
	{
		System.out.println("Performing pointer in pointer search...");

		MemoryDump memoryDump = memoryDumps.get(0);

		for (int currentOffset = 0; currentOffset < memoryDump.getSize(); currentOffset += 4)
		{
			int readValue = memoryDump.readPointerValue(currentOffset);

			if (readValue == INVALID_POINTER)
			{
				continue;
			}

			int pointerOffset = readValue - startingOffset;

			int startingIndex;
			int lastIndex;

			if (allowNegativeOffsets)
			{
				startingIndex = -maximumPointerOffset / 4;
				lastIndex = maximumPointerOffset / 4;
			} else
			{
				startingIndex = 0;
				lastIndex = maximumPointerOffset / 2;
			}

			for (int innerOffsetIndex = startingIndex; innerOffsetIndex < lastIndex; innerOffsetIndex += 4)
			{
				int innerBaseOffset = pointerOffset + innerOffsetIndex;

				if (innerBaseOffset < 0)
				{
					innerOffsetIndex += (-innerBaseOffset);
					innerBaseOffset = 0;
				}

				if (!memoryDump.getMemoryBounds().isValidMemoryAddress(
						innerBaseOffset + startingOffset))
				{
					continue;
				}

				readValue = memoryDump.readPointerValue(innerBaseOffset);

				if (readValue == INVALID_POINTER)
				{
					continue;
				} else
				{
					MemoryPointer memoryPointer = new MemoryPointer(
							currentOffset, startingIndex);
					memoryPointer.addPointerOffset(innerOffsetIndex);
					int outerOffset = memoryDump.getTargetAddress() - readValue;

					memoryPointer.addPointerOffset(outerOffset);

					if (isAllowed(outerOffset))
					{
						System.out.println(memoryPointer);
					}
				}
			}
		}
	}

	/**
	 * @param pointerOffset
	 *            The pointer offset to check
	 * @return True if the pointer offset passes all tests false otherwise
	 */
	private boolean isAllowed(int pointerOffset)
	{
		if (pointerOffset == INVALID_POINTER)
		{
			return false;
		}

		if (!isWithinOffsetBounds(pointerOffset))
		{
			return false;
		}

		if (!fulfillsNegativeOffsetsConstraint(pointerOffset))
		{
			return false;
		}

		return true;
	}

	private boolean fulfillsNegativeOffsetsConstraint(int pointerOffset)
	{
		return allowNegativeOffsets || pointerOffset > 0;
	}

	private boolean isWithinOffsetBounds(int pointerOffset)
	{
		return Math.abs(pointerOffset) < maximumPointerOffset;
	}

	public void setMaximumPointerOffset(int maximumPointerOffset)
	{
		this.maximumPointerOffset = maximumPointerOffset;
	}

	public int getMaximumPointerOffset()
	{
		return maximumPointerOffset;
	}

	public void setAllowNegativeOffsets(boolean allowNegativeOffsets)
	{
		this.allowNegativeOffsets = allowNegativeOffsets;
	}

	public boolean getAllowNegativeOffsets()
	{
		return allowNegativeOffsets;
	}
}