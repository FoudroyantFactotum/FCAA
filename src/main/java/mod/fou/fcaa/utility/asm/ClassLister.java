package mod.fou.fcaa.utility.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassLister implements IClassTransformer
{
    @Override
    public byte[] transform(String s, String s1, byte[] bytes)
    {
        if (s.startsWith("mod.fou.fcaa"))
            System.out.println(s);

        return bytes;
    }
}
