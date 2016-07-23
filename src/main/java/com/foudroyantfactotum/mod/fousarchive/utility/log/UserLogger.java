/*
 * Fou's Archive - A Minecraft Mod
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
