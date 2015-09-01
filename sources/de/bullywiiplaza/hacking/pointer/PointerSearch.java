package de.bullywiiplaza.hacking.pointer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.bullywiiplaza.hacking.pointer.memory.MemoryDump;
import de.bullywiiplaza.hacking.pointer.memory.MemoryPointer;
import de.bullywiiplaza.hacking.pointer.utilities.BinaryFilesDetector;

public abstract class PointerSearch
{
	private int maximumPointerOffset;
	private boolean allowNegativeOffsets;
	private List<MemoryDump> memoryDumps;
	private int startingOffset;

	public static final int INVALID_POINTER = -1;

	public PointerSearch(String memoryDumpsFolder, int startingOffset)
			throws IOException
	{
		maximumPointerOffset = 0x1000;
		allowNegativeOffsets = false;
		memoryDumps = new ArrayList<>();
		List<File> binaryFiles = BinaryFilesDetector
				.getBinaryFiles(memoryDumpsFolder);

		System.out.println("Reading memory dumps...");

		for (File binaryFile : binaryFiles)
		{
			MemoryDump memoryDump = new MemoryDump(binaryFile.getAbsolutePath());
			MemoryPointer memoryPointer = new MemoryPointer(startingOffset,
					memoryDump.getSize());
			memoryDump.setMemoryPointer(memoryPointer);

			memoryDumps.add(memoryDump);
		}

		this.startingOffset = startingOffset;
	}

	public void performPointerSearch()
	{
		MemoryDump firstMemoryDump = memoryDumps.get(0);
		MemoryDump secondMemoryDump = memoryDumps.get(1);

		System.out.println("Performing pointer search...");

		for (int memoryDumpOffsetIndex = 0; memoryDumpOffsetIndex < firstMemoryDump
				.getSize() / 4; memoryDumpOffsetIndex++)
		{
			int baseOffset = memoryDumpOffsetIndex * 4;

			int firstDifference = getDifferenceBetweenPointerValueAndTargetAddress(
					firstMemoryDump, baseOffset);

			if (firstDifference == INVALID_POINTER)
			{
				continue;
			}

			if (!isWithinOffsetBounds(firstDifference))
			{
				continue;
			}

			if (!fulfillsNegativeOffsetsConstraint(firstDifference))
			{
				continue;
			}

			int secondDifference = getDifferenceBetweenPointerValueAndTargetAddress(
					secondMemoryDump, baseOffset);

			if (firstDifference == secondDifference)
			{
				if (isCompatibleWithAllRemainingMemoryDumps(baseOffset,
						firstDifference))
				{
					String hexadecimalPointerRepresenation = getHexadecimalPointerRepresentation(
							baseOffset, firstDifference);

					System.out.println(hexadecimalPointerRepresenation);
				}
			}
		}
	}

	private boolean fulfillsNegativeOffsetsConstraint(int pointerOffset)
	{
		return allowNegativeOffsets || pointerOffset > 0;
	}

	private boolean isWithinOffsetBounds(int pointerOffset)
	{
		return Math.abs(pointerOffset) < maximumPointerOffset;
	}

	private String getHexadecimalPointerRepresentation(int baseOffset,
			int pointerOffset)
	{
		StringBuilder pointerBuilder = new StringBuilder();
		pointerBuilder.append("[");
		pointerBuilder
				.append(new HexadecimalNumber(startingOffset + baseOffset));
		pointerBuilder.append("] ");

		HexadecimalNumber pointerOffsetHexadecimal = new HexadecimalNumber(
				pointerOffset);

		if (!pointerOffsetHexadecimal.isNegative())
		{
			pointerBuilder.append("+ ");
		}

		pointerBuilder.append(pointerOffsetHexadecimal);

		return pointerBuilder.toString();
	}

	private boolean isCompatibleWithAllRemainingMemoryDumps(int baseOffset,
			int pointerOffset)
	{
		for (int memoryDumpsIndex = 2; memoryDumpsIndex < memoryDumps.size(); memoryDumpsIndex++)
		{
			int pointerValue = memoryDumps.get(memoryDumpsIndex).readValueAt(
					baseOffset);
			int pointerDestination = pointerValue + pointerOffset;
			int targetAddress = memoryDumps.get(memoryDumpsIndex)
					.getTargetAddress();

			if (pointerDestination != targetAddress)
			{
				return false;
			}
		}

		return true;
	}

	public int getDifferenceBetweenPointerValueAndTargetAddress(
			MemoryDump memoryDump, int baseOffset)
	{
		int readValue = memoryDump.readValueAt(baseOffset);

		if (!memoryDump.getMemoryPointer().isValidPointer(readValue))
		{
			return INVALID_POINTER;
		}

		int difference = memoryDump.getTargetAddress() - readValue;

		return difference;
	}

	public void setMaximumPointerOffset(int maximumPointerOffset)
	{
		this.maximumPointerOffset = maximumPointerOffset;
	}

	public void setAllowNegativeOffsets(boolean allowNegativeOffsets)
	{
		this.allowNegativeOffsets = allowNegativeOffsets;
	}
}