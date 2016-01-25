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
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import com.foudroyantfactotum.mod.fousarchive.proxy.RenderProxy;
import com.foudroyantfactotum.mod.fousarchive.structure.coordinates.TransformLAG;
import com.foudroyantfactotum.mod.fousarchive.structure.registry.StructureRegistry;
import com.foudroyantfactotum.mod.fousarchive.utility.Log.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

    @SidedProxy(clientSide = "mod.fou.fcaa.proxy.ClientRenderProxy", serverSide = "mod.fou.fcaa.proxy.RenderProxy")
    public static RenderProxy render;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Logger.info("==========preInit==========");
        OBJLoader.instance.addDomain(MOD_ID);
        TransformLAG.initStatic();
        InitBlock.init();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event)
    {
        StructureRegistry.loadRegisteredPatterns();
        Minecraft.getMinecraft().getTextureManager()
                .loadTexture(
                        midiimg,
                        new MidiTexture(midiFile)
                );
    }

    public static final ResourceLocation midiFile = new ResourceLocation("fcaa", "midi/Hindustan(1918).mid");
    public static final ResourceLocation midiimg = new ResourceLocation("fcaa", "midi/Hindustan(1918).img");
}
