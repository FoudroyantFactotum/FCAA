/*
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */
package mod.fou.fcaa.structure.registry;

import com.google.common.collect.Lists;
import mod.fou.fcaa.Blocks.Structure.BlockStructure;
import mod.fou.fcaa.utility.Log.Logger;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.ProgressManager;

import java.lang.reflect.Field;
import java.util.*;

import static mod.fou.fcaa.structure.StructureDefinitionBuilder.StructureDefinitionError;
import static net.minecraftforge.fml.common.ProgressManager.ProgressBar;

public final class StructureRegistry
{
    private static ProgressBar blockBar = null;
    private static Map<Integer, BlockStructure> structures = new HashMap<>();
    private static List<BlockStructure> registeredStructures = new LinkedList<>();

    /***
     * Register structures here for loading into system.
     *
     * @param structure structure to be registered.
     */
    public static void registerStructureForLoad(BlockStructure structure)
    {
        registeredStructures.add(structure);
    }

    /***
     * loadRegisteredPatterns() is called on onFMLInitialization after all blocks have been loaded required so that
     * blocks of other mod can be used within the structure.
     */
    public static void loadRegisteredPatterns()
    {
        blockBar = ProgressManager.push("Structure", registeredStructures.size());

        try
        {
            final Field structurePattern = BlockStructure.class.getDeclaredField("structureDefinition");
            structurePattern.setAccessible(true);

            final Field regHash = BlockStructure.class.getDeclaredField("regHash");
            regHash.setAccessible(true);

            for (final BlockStructure block : registeredStructures)
            {
                blockBar.step(block.getLocalizedName());

                try
                {
                    structurePattern.set(block, block.getStructureBuild().build());
                    regHash.set(block, block.getUnlocalizedName().hashCode());

                    structures.put(block.getUnlocalizedName().hashCode(), block);
                } catch (StructureDefinitionError e)
                {
                    throw new StructureDefinitionError(e.getMessage() + " on '" + block.getUnlocalizedName() + '\'');
                }
            }

            ProgressManager.pop(blockBar);
            blockBar = null;

            Logger.info("Analytical Engine constructed " + structures.size() + " noteworthy contraptions");

        } catch (NoSuchFieldException e)
        {
            Logger.info("\n\n\nNoSuchFieldException: " + e.getLocalizedMessage() + "\n\n\n");
        } catch (IllegalAccessException e)
        {
            Logger.info("\n\n\nIllegalAccessException: " + e.getLocalizedMessage() + "\n\n\n");
        }
    }

    private StructureRegistry()
    {
        //no op
    }

    public static Collection<BlockStructure> getStructureList()
    {
        return structures.values();
    }

    public static BlockStructure getStructureBlock(int hash)
    {
        return structures.get(hash);
    }

    /***
     * Command (class) for reloading the structures in-game after jvm hot swap
     */
    public static class CommandReloadStructures implements ICommand
    {
        @Override
        public String getCommandName()
        {
            return "RELOAD_STRUCTURES";
        }

        @Override
        public String getCommandUsage(ICommandSender player)
        {
            return "RELOAD_STRUCTURES (That's all there is, there isn't any more.)";
        }

        @Override
        public List getCommandAliases()
        {
            return Lists.newArrayList("RELOAD_STRUCTURES");
        }

        @Override
        public void processCommand(ICommandSender player, String[] args)
        {
            loadRegisteredPatterns();
            player.addChatMessage(new ChatComponentText("Analytical Engine reconstructed " + structures.size() + " noteworthy contraptions"));
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender player)
        {
            return true;
        }

        @Override
        public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
        {
            return null;
        }

        @Override
        public boolean isUsernameIndex(String[] args, int index)
        {
            return false;
        }

        @Override
        public int compareTo(ICommand iCommand)
        {
            return 0;
        }
    }
}
