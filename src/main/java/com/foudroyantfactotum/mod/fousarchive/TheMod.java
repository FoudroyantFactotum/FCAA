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
package com.foudroyantfactotum.mod.fousarchive;

import com.foudroyantfactotum.mod.fousarchive.init.InitBlock;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.proxy.RenderProxy;
import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import com.foudroyantfactotum.tool.structure.StructureRegistry;
import com.foudroyantfactotum.tool.structure.coordinates.TransformLAG;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.IOException;

import static net.minecraftforge.fml.common.Mod.EventHandler;
import static net.minecraftforge.fml.common.Mod.Instance;

@Mod(modid = TheMod.MOD_ID, name = TheMod.MOD_NAME, version = TheMod.MOD_VERSION, useMetadata = true)
public class TheMod
{
    public static final String MOD_ID = "fousarchive";
    public static final String MOD_NAME = "Fou's Archive";
    public static final String MOD_VERSION = "0.1";

    @Instance
    public static TheMod instance;

    @SidedProxy(
            clientSide = "com.foudroyantfactotum.mod.fousarchive.proxy.ClientRenderProxy",
            serverSide = "com.foudroyantfactotum.mod.fousarchive.proxy.RenderProxy")
    public static RenderProxy render;

    private static File sourceLocation;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Logger.info("==========preInit==========");
        OBJLoader.instance.addDomain(MOD_ID);
        TransformLAG.initStatic();
        InitBlock.init();

        sourceLocation = event.getSourceFile();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) throws IOException
    {
        StructureRegistry.loadRegisteredPatterns();
        ItemPianoRoll.init();

        ItemPianoRoll.INSTANCE = new ItemPianoRoll();
        ItemPianoRoll.INSTANCE.setCreativeTab(InitBlock.ModTab.tabs.get(InitBlock.ModTab.main));
        GameRegistry.registerItem(ItemPianoRoll.INSTANCE, ItemPianoRoll.INSTANCE.getUnlocalizedName());
    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ItemPianoRoll.CommandPianoRollID());
    }
}
