package com.foudroyantfactotum.mod.fousarchive.utility;

import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class Settings
{
    public static final int VERSION = 0;

    private static Configuration c;

    public static void setConfigurationFile(File f)
    {
        c = new Configuration(f, String.valueOf(VERSION));

        if (isCurrentVersion())
        {
            loadConfiguration(c, PianoPlayer.class);

            if (c.hasChanged())
                c.save();
        } else
        {
            throw new FousArchiveException("Incorrect version of config file. Please delete the config to regenerate as current version");
        }
    }

    private static boolean isCurrentVersion()
    {
        try
        {
            final int versionNo = Integer.decode(c.getLoadedConfigVersion());

            return versionNo == VERSION;
        } catch (NumberFormatException e)
        {//unable to determine current version
            return false;
        }
    }

    @FunctionalInterface
    private interface DFunction2<One, Two> {
        void setProperty(One one, Two two) throws IllegalAccessException;
    }

    private static final Map<Class<?>, DFunction2<String, Field>> f2p = new HashMap<>();

    static
    {
        f2p.put(int.class,      (cat, field) -> field.setInt(    null,         c.get(cat, field.getName(),            field.getInt(    null)).getInt()));
        f2p.put(short.class,    (cat, field) -> field.setShort(  null, (short) c.get(cat, field.getName(),            field.getShort(  null)).getInt()));
        f2p.put(byte.class,     (cat, field) -> field.setByte(   null, (byte)  c.get(cat, field.getName(),            field.getByte(   null)).getInt()));
        f2p.put(boolean.class,  (cat, field) -> field.setBoolean(null,         c.get(cat, field.getName(),            field.getBoolean(null)).getBoolean()));
        f2p.put(double.class,   (cat, field) -> field.setDouble( null,         c.get(cat, field.getName(),            field.getDouble( null)).getDouble()));
        f2p.put(String.class,   (cat, field) -> field.set(       null,         c.get(cat, field.getName(), (String)   field.get(       null)).getString()));
        f2p.put(String[].class, (cat, field) -> field.set(       null,         c.get(cat, field.getName(), (String[]) field.get(       null)).getStringList()));
    }

    private static void loadConfiguration(@Nonnull Configuration c, @Nonnull Class<?> clazz)
    {
        final String category = getSpacedLocName(clazz.getSimpleName());

        for (final Field f : clazz.getFields())
        {
            try
            {
                if (!Modifier.isStatic(f.getModifiers())) continue;

                if (f2p.containsKey(f.getType()))
                    f2p.get(f.getType()).setProperty(category, f);
                else
                    Logger.info("Unable to create prop for " + f.getName());

            } catch (IllegalAccessException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static String getSpacedLocName(@Nonnull String s)
    {
        final StringBuilder sb = new StringBuilder(s.length() * 2 - 1);

        for (final char c : s.toCharArray())
        {
            if (Character.isUpperCase(c))
                sb.append(' ');

            sb.append(c);
        }

        return sb.toString().trim().replace(' ', '_');
    }

    public static class PianoPlayer
    {
        public static int b7_max_vol = 127;
        public static int ums_texture_unload_time = 5000;
        public static int ums_texture_poll_time = 5000;
        public static int uy_max_texture_cap = 8048;
        public static int uy_max_sheet_shown = 500;
        public static double d_key_restore_time = 0.001;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException
    {
        c = new Configuration();
        final Field f = Configuration.class.getDeclaredField("file");
        f.setAccessible(true);
        f.set(c, new File("/tmp/config"));

        loadConfiguration(c, PianoPlayer.class);
        c.save();
    }
}
