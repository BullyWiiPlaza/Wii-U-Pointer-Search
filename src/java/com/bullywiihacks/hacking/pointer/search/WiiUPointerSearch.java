package com.bullywiihacks.hacking.pointer.search;

import java.io.IOException;
import java.util.List;

import com.bullywiihacks.hacking.pointer.memory.MemoryDump;

/**
 * An instance of PointerSearch specifically for the Nintendo Wii U console memory range
 */
public class WiiUPointerSearch extends PointerSearch
{
	public WiiUPointerSearch(List<MemoryDump> memoryDumps) throws IOException
	{
		super(memoryDumps, 0x10000000);
	}
}