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
