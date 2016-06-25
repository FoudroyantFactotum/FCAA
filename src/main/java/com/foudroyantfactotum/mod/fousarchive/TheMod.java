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

import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import com.foudroyantfactotum.mod.fousarchive.init.IMCEvents;
import com.foudroyantfactotum.mod.fousarchive.init.InitBlock;
import com.foudroyantfactotum.mod.fousarchive.init.InitCommand;
import com.foudroyantfactotum.mod.fousarchive.init.InitItem;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.JsonMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiMultiplexSynth;
import com.foudroyantfactotum.mod.fousarchive.proxy.IModRenderProxy;
import com.foudroyantfactotum.mod.fousarchive.utility.Settings;
import com.foudroyantfactotum.tool.structure.StructureRegistry;
import com.foudroyantfactotum.tool.structure.coordinates.TransformLAG;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;

import static net.minecraftforge.fml.common.Mod.EventHandler;
import static net.minecraftforge.fml.common.Mod.Instance;

@Mod(modid = TheMod.MOD_ID, name = TheMod.MOD_NAME, version = TheMod.MOD_VERSION, useMetadata = true)
public class TheMod
{
    public static final boolean _DEBUG_MODE = true;
    public static final String MOD_ID = "fousarchive";
    public static final String MOD_NAME = "Fou's Archive";
    public static final String MOD_VERSION = "0.1";

    @Instance
    public static TheMod instance;

    @SidedProxy(
            clientSide = "com.foudroyantfactotum.mod.fousarchive.proxy.ClientRenderProxy",
            serverSide = "com.foudroyantfactotum.mod.fousarchive.proxy.ServerRenderProxy")
    public static IModRenderProxy proxy;

    public static final Gson json = new GsonBuilder()
            .registerTypeAdapter(JsonMidiDetails.class, JsonMidiDetails.Json.JSD)
            .setPrettyPrinting()
            .create();

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        StructureRegistry.setMOD_ID(TheMod.MOD_ID);

        Settings.setConfigurationFile(event.getSuggestedConfigurationFile());

        TransformLAG.initStatic();
        InitBlock.init();
        InitItem.init();

        proxy.preInit();

        try
        {
            //init one Synth to fix audio crackle on start
            MidiMultiplexSynth.INSTANCE.getNewReceiver().close();
        } catch (MidiUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) throws IOException
    {
        StructureRegistry.loadRegisteredPatterns();

        if (event.getSide() == Side.CLIENT)
        {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                 if (tintIndex == 1)
                 {
                     final NBTTagCompound nbt = stack.getTagCompound();

                     if (nbt != null && nbt.hasKey(ItemPianoRoll.COLOUR))
                         return nbt.getInteger(ItemPianoRoll.COLOUR);
                 }
                return 0xFFFFFF;
            }, ModItems.pianoRoll);
        }

        /*final RandomChestPianoRoll rcpp = new RandomChestPianoRoll();

        ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.NETHER_FORTRESS).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(rcpp);
        ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(rcpp);*/

        //load internal music files
        FMLInterModComms.sendMessage(
                TheMod.MOD_ID,
                IMCEvents.IMCEventHandler.REGISTER_JSON_SONG_LIST.key(),
                new ResourceLocation(TheMod.MOD_ID, "midi/master.json.gz").toString()
        );

        proxy.init();
    }

    @EventHandler
    public static void imcEvent(FMLInterModComms.IMCEvent event)
    {
        IMCEvents.eventProcess(event.getMessages());
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event)
    {
        InitCommand.init(event);
    }
}
