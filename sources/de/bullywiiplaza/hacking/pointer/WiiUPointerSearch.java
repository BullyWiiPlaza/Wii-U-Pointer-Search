package de.bullywiiplaza.hacking.pointer;

import java.io.IOException;

public class WiiUPointerSearch extends PointerSearch
{
	public WiiUPointerSearch(String memoryDumpsFolder) throws IOException
	{
		super(memoryDumpsFolder, 0x10000000);
	}
}