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
package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.command.CommandPianoRollID;
import com.foudroyantfactotum.tool.structure.StructureRegistry;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import static com.foudroyantfactotum.mod.fousarchive.TheMod._DEBUG_MODE;

public final class InitCommand
{
    private InitCommand()
    {
        //noop
    }

    public static void init(final FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandPianoRollID());

        if (_DEBUG_MODE)
        {
            event.registerServerCommand(new StructureRegistry.CommandReloadStructures());
        }
    }
}
