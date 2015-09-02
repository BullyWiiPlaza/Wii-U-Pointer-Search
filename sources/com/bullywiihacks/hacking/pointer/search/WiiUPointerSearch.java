package com.bullywiihacks.hacking.pointer.search;

import java.io.IOException;

/**
 * An instance of PointerSearch specifically for the Nintendo Wii U console memory range
 */
public class WiiUPointerSearch extends PointerSearch
{
	public WiiUPointerSearch(String memoryDumpsFolder) throws IOException
	{
		super(memoryDumpsFolder, 0x10000000);
	}
}