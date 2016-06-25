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
package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.library.ModBlocks;
import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import com.foudroyantfactotum.mod.fousarchive.library.ModNames;
import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_ShapeTE;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.TEPlayerPiano;
import com.foudroyantfactotum.tool.structure.StructureRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class InitBlock
{
    public static class ModTab
    {
        public static final ImmutableMap <String, CreativeTabs> tabs;

        public static final String main = "main";
        public static final String none = "none"; //no tab

         static
         {
            ImmutableMap.Builder<String, CreativeTabs> builder = ImmutableMap.builder();

            builder.put(main, new CreativeTabs(TheMod.MOD_ID)
            {
                @Override
                public Item getTabIconItem()
                {
                    return Items.GOLDEN_APPLE;
                }
            });

             tabs = builder.build();
        }
    }

    public static void init()
    {
        registerBlocks();
        registerStructures();
    }

    private static void registerBlocks()
    {
        GameRegistry.register(ModBlocks.structureShape);
        GameRegistry.registerWithItem(ModBlocks.playerPiano);

        GameRegistry.registerTileEntity(TEPlayerPiano.class, ModNames.Blocks.playerPiano);
        GameRegistry.registerTileEntity(FA_ShapeTE.class, ModNames.Blocks.structureShape);

        final CreativeTabs tab = ModTab.tabs.get(ModTab.main);

        ModBlocks.playerPiano.setCreativeTab(tab);
        ModItems.tuningFork.setCreativeTab(tab);
    }

    private static void registerStructures()
    {
        StructureRegistry.registerStructureForLoad(ModBlocks.playerPiano, ModBlocks.structureShape);
        TheMod.proxy.registerBlockAsItemModel(ModBlocks.playerPiano);
    }
}
