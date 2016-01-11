package mod.fou.fcaa.utility;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;

public class Clazz
{
    public static ImmutableSet<ClassPath.ClassInfo> getClassListFrom(Package p)
    {
        try
        {
            return ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getTopLevelClassesRecursive(p.getName());
        } catch (IOException e)
        {
            return ImmutableSet.of();
        }
    }
}
