package com.foudroyantfactotum.mod.fousarchive.utility.log;

import com.foudroyantfactotum.mod.fousarchive.utility.Settings;

import java.lang.reflect.Field;

public enum UserLogger
{
    GENERAL,
    MIDI_PIANO;

    private Field f_parm;

    UserLogger()
    {
        try
        {
            f_parm = Settings.DebugLog.class.getField("b_"+this.name().toLowerCase());
        } catch (NoSuchFieldException e)
        {
            Logger.fatal("UserLogger missing field " + this, e);
        }
    }

    public boolean canDebug()
    {
        try
        {
            return f_parm != null && f_parm.getBoolean(null);
        } catch (IllegalAccessException e)
        {
            Logger.fatal("UserLogger can not access field " + this, e);
            return false;
        }
    }
}
