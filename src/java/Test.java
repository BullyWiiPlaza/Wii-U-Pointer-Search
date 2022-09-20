import java.io.File;
import java.io.IOException;

import com.bullywiihacks.hacking.pointer.IOUtilities;
import com.bullywiihacks.hacking.pointer.memory.MemoryBounds;
import com.bullywiihacks.hacking.pointer.memory.MemoryDump;
import com.bullywiihacks.hacking.pointer.search.MemoryPointer;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		MemoryPointer memoryPointer = new MemoryPointer(0x44, 0x10000000);
		memoryPointer.setStartingOffset(0x10000000);
		memoryPointer.addPointerOffset(-0x4);
		memoryPointer.addPointerOffset(0xC);

		System.out.println(memoryPointer);

		String memoryDumpPath = IOUtilities
				.getWorkingDirectory().getAbsolutePath()
				+ File.separator
				+ "examples\\Pointer in Pointer\\1000001C.bin";
		MemoryDump memoryDump = new MemoryDump(memoryDumpPath);
		MemoryBounds memoryBounds = new MemoryBounds(0x10000000, memoryDump.getSize());
		memoryDump.setMemoryBounds(memoryBounds);

		System.out.println(memoryPointer.followPointer(memoryDump));
	}
}
