For a compiled version, check out the [release thread][1] on GBAtemp.

---

This application can find memory pointers on Wii U games. The selected folder must contain `.bin` binary files and their names have to be the hexadecimal representation of the respective `target address`. "Unlimited" extra memory dumps are supported so feel free to place them all into the folder but mind the naming convention.

The `examples` directory in this repository contains two example memory dumps. When a pointer search is performed on them, it should return a single pointer result:<br>
`[10000020] + 78`

This result is to be interpreted as follows:<br>
Go to address `10000020` and take its value. Add `78` and the result should be equal to the `target address`. Note that Wii U memory ranges start at `10000000` which is the reason why the address is `20 + 10000000 = 10000020` instead of being just the file offset of `20`.

Searches can be fine-tuned via the `Options` menu. Select whether you want to include results with `negative offsets` and set an upper threshold for the `maximum pointer offset`. Pointers are less likely to be reliable when their offsets are high or negative so you should aim for small and positive offsets if possible.

So far `pointer in pointer` searches are not supported but may be soon.

Note:<br>
If the application stops after reading the memory dumps it means that you ran out of memory. Make sure that you close most other applications to have enough RAM available. This application is very memory-hungry since Wii U memory dumps are huge.

[1]: http://gbatemp.net/threads/wii-u-pointer-search.396232/