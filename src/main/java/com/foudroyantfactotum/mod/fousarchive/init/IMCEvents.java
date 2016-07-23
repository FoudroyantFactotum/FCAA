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

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.JsonMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

public final class IMCEvents
{
    public enum IMCEventHandler
    {
        REGISTER_JSON_SONG_LIST("register.playerPiano.roll.list.json", IMCEvents::addExternalSongList);

        private final Consumer<IMCMessage> m;
        private final String s;

        IMCEventHandler(String s, Consumer<IMCMessage> m)
        {
            this.s = s;
            this.m = m;
        }

        public void handleMsg(IMCMessage msg)
        {
            m.accept(msg);
        }

        public String key()
        {
            return s;
        }
    }

    private static final ImmutableMap<String, IMCEventHandler> eventHandler;

    static
    {
        ImmutableMap.Builder<String, IMCEventHandler> builder = ImmutableMap.builder();

        for (IMCEventHandler e : IMCEventHandler.values())
            builder.put(e.key(), e);

        eventHandler = builder.build();
    }

    public static void eventProcess(@Nonnull ImmutableList<IMCMessage> msgs)
    {
        for (IMCMessage msg : msgs)
        {
            if (eventHandler.containsKey(msg.key))
            {
                try
                {
                    eventHandler.get(msg.key).handleMsg(msg);
                } catch (FousArchiveException e)
                {
                    throw new FousArchiveException("IMCMsg " + msg.key + " : " + msg.getSender(), e);
                }
            }
        }
    }

    private static void addExternalSongList(@Nonnull IMCMessage msg)
    {
        if (msg.isStringMessage())
        {
            final ResourceLocation rl = new ResourceLocation(msg.getStringValue());

            try (final GZIPInputStream stream = new GZIPInputStream(TheMod.proxy.getInputStream(rl)))
            {
                final Reader r = new InputStreamReader(stream);
                final JsonMidiDetails jmd = TheMod.json.fromJson(r, JsonMidiDetails.class);

                for (final Map.Entry<ResourceLocation, ImmutableMap<String, String>> entry : jmd.midiDetails.entrySet())
                {
                    if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
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
    }
}
