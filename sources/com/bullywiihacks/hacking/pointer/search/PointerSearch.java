package com.bullywiihacks.hacking.pointer.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bullywiihacks.hacking.pointer.memory.MemoryDump;
import com.bullywiihacks.hacking.pointer.memory.MemoryBounds;
import com.bullywiihacks.hacking.pointer.utilities.files.BinaryFilesDetector;

/**
 * A class for performing pointer searches on memory dumps
 */
public abstract class PointerSearch
{
	private int maximumPointerOffset;
	private boolean allowNegativeOffsets;
	private List<MemoryDump> memoryDumps;

	public static final int DEFAULT_MAXIMUM_POINTER_OFFSET = 0x1000;
	public static final boolean DEFAULT_ALLOW_NEGATIVE_OFFSETS = false;

	public static final int INVALID_POINTER = -1;

	public PointerSearch(String memoryDumpsFolder, int startingOffset)
			throws IOException
	{
		maximumPointerOffset = DEFAULT_MAXIMUM_POINTER_OFFSET;
		allowNegativeOffsets = DEFAULT_ALLOW_NEGATIVE_OFFSETS;
		memoryDumps = new ArrayList<>();
		List<File> binaryFiles = BinaryFilesDetector
				.getBinaryFiles(memoryDumpsFolder);

		System.out.println("Reading memory dumps...");

		for (File binaryFile : binaryFiles)
		{
			MemoryDump memoryDump = new MemoryDump(binaryFile.getAbsolutePath());
			MemoryBounds memoryBounds = new MemoryBounds(startingOffset,
					memoryDump.getSize());
			memoryDump.setMemoryBounds(memoryBounds);

			memoryDumps.add(memoryDump);
		}
	}

	/**
	 * Implements the logic of performing a pointer search
	 */
	public void performPointerSearch()
	{
		System.out.println("Performing pointer search...");

		MemoryDump memoryDump = memoryDumps.get(0);

		for (int baseOffset = 0; baseOffset < memoryDump.getSize(); baseOffset += 4)
		{
			int readValue = memoryDump.readValueAt(baseOffset);

			if (!memoryDump.getMemoryBounds().isValidMemoryAddress(readValue))
			{
				continue;
			}

			int pointerOffset = memoryDump.getTargetAddress() - readValue;

			if (!isAllowed(pointerOffset))
			{
				continue;
			}

			MemoryPointer memoryPointer = new MemoryPointer(baseOffset);
			memoryPointer.addPointerOffset(pointerOffset);
			memoryPointer.setStartingOffset(memoryDump.getMemoryBounds().getStartingOffset());

			if (memoryPointer.reachesTargetAddresses(memoryDumps))
			{
				System.out.println(memoryPointer);
			}
		}
	}

	public void performPointerInPointerSearch()
	{
		int memoryDumpsIndex = 0;

		MemoryDump memoryDump1 = memoryDumps.get(memoryDumpsIndex++);

		System.out.println("Performing pointer in pointer search...");

		// We start later to prevent memory issues
		for (int baseOffset = 0x2E000000; baseOffset < memoryDump1.getSize(); baseOffset += 4)
		{
			int readValue = memoryDump1.readValueAt(baseOffset);

			if (!memoryDump1.getMemoryBounds().isValidMemoryAddress(readValue))
			{
				continue;
			}

			int pointerOffset = readValue
					- memoryDump1.getMemoryBounds().getStartingOffset();

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

				readValue = memoryDump1.readValueAt(pointerOffset
						+ innerOffsetIndex);

				if (!memoryDump1.getMemoryBounds().isValidMemoryAddress(
						readValue))
				{
					continue;
				} else
				{
					MemoryPointer memoryPointer = new MemoryPointer(baseOffset);
					memoryPointer.addPointerOffset(innerOffsetIndex);
					memoryPointer.setStartingOffset(memoryDump1
							.getMemoryBounds().getStartingOffset());
					int outerOffset = memoryDump1.getTargetAddress()
							- readValue;
					memoryPointer.addPointerOffset(outerOffset);

					if (Math.abs(outerOffset) <= maximumPointerOffset)
					{
						if(allowNegativeOffsets || outerOffset > 0)
						{
							System.out.println(memoryPointer);
						}
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