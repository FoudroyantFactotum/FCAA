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
import com.foudroyantfactotum.mod.fousarchive.midi.JsonMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import com.foudroyantfactotum.mod.fousarchive.proxy.RenderProxy;
import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;
import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import com.foudroyantfactotum.tool.structure.StructureRegistry;
import com.foudroyantfactotum.tool.structure.coordinates.TransformLAG;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public static final Gson JSON = new GsonBuilder()
            .registerTypeAdapter(JsonMidiDetails.class, JsonMidiDetails.Json.JSD)
            .setPrettyPrinting()
            .create();

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IOException
    {
        Logger.info("==========preInit==========");
        OBJLoader.instance.addDomain(MOD_ID);
        TransformLAG.initStatic();
        InitBlock.init();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) throws IOException
    {
        StructureRegistry.loadRegisteredPatterns();

        ItemPianoRoll.INSTANCE = new ItemPianoRoll();
        ItemPianoRoll.INSTANCE.setCreativeTab(InitBlock.ModTab.tabs.get(InitBlock.ModTab.main));
        GameRegistry.registerItem(ItemPianoRoll.INSTANCE, ItemPianoRoll.INSTANCE.getUnlocalizedName());
        FMLInterModComms.sendMessage(TheMod.MOD_ID, "register.piano.roll.list", new ResourceLocation(TheMod.MOD_ID, "midi/master.json.gz").toString());
    }

    @EventHandler
    public static void imcEvent(FMLInterModComms.IMCEvent event)
    {
        for (final FMLInterModComms.IMCMessage msg : event.getMessages())
        {
            if (msg.key.equals("register.piano.roll.list"))
            {
                try
                {
                    if (msg.isStringMessage())
                    {
                        final ResourceLocation rl = new ResourceLocation(msg.getStringValue());

                        try (final GZIPInputStream stream = new GZIPInputStream(Minecraft.getMinecraft().getResourceManager()
                                .getResource(rl).getInputStream()))
                        {
                            final Reader r = new InputStreamReader(stream);
                            final JsonMidiDetails jmd = JSON.fromJson(r, JsonMidiDetails.class);

                            for (final Map.Entry<ResourceLocation, ImmutableMap<String, String>> entry : jmd.midiDetails.entrySet())
                            {
                                LiveImage.INSTANCE.registerSong(new MidiTexture(entry.getKey()));
                                ItemPianoRoll.addPianoRoll(entry.getKey());
                                LiveMidiDetails.INSTANCE.addSongDetails(entry.getKey(), MidiDetails.fromMap(entry.getValue()));
                            }

                        } catch (IOException e)
                        {
                            throw new FousArchiveException("Failed to open master list ", e);
                        }

                    } else
                    {
                        throw new FousArchiveException("Received " + msg.getMessageType() + " : expected String");
                    }
                } catch (FousArchiveException e)
                {
                    throw new FousArchiveException("IMCMsg register.piano.roll.list " + msg.getSender(), e);
                }
            }
        }
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {

    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ItemPianoRoll.CommandPianoRollID());
    }

    private static final String midaddr = "src/main/resources/assets/" + TheMod.MOD_ID;
    private static final String assetDir = "/midi/";

    public static void main(String[] args)
    {
        final List<File> list = new LinkedList<>();
        final File dir = new File(midaddr + assetDir);

        Logger.info(dir.getAbsolutePath());

        for (File sfile : dir.listFiles())
        {
            recFile(sfile, list);
        }

        final ImmutableMap.Builder<ResourceLocation, ImmutableMap<String, String>> builder = ImmutableMap.builder();

        for (File f : list)
        {
            final String addr = f.getPath();
            final String rladdr = TheMod.MOD_ID + ':' + addr.substring(addr.indexOf(midaddr) + midaddr.length()+1);
            final ImmutableMap<String, String> details = midiDetailsToMap(f);

            if (details != null)
            {
                builder.put(new ResourceLocation(rladdr), details);
            } else {
                Logger.info("Missing details on " + f);
            }
        }

        try (final OutputStream fstream = new FileOutputStream(dir + "/master.json.gz"))
        {
            try (final GZIPOutputStream gzstream = new GZIPOutputStream(fstream))
            {
                final OutputStreamWriter osw = new OutputStreamWriter(gzstream);

                osw.write(JSON.toJson(builder.build()));
                osw.flush();
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static ImmutableMap<String, String> midiDetailsToMap(File f)
    {
        try (final InputStream stream = new FileInputStream(f))
        {
            final MidiDetails md = MidiDetails.getMidiDetails(stream);

            if (md != MidiDetails.NO_DETAILS)
            {
                return md.toMap();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static void recFile(File file, List<File> list)
    {
        if (file.isDirectory())
        {
            for (final File sfile : file.listFiles())
            {
                recFile(sfile, list);
            }
        } else {
            if (file.getName().toLowerCase().endsWith(".mid"))
            {
                list.add(file);
            }
        }
    }
}
