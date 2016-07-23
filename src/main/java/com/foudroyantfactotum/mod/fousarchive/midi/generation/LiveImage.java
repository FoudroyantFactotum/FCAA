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
package com.foudroyantfactotum.mod.fousarchive.midi.generation;

import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.utility.Settings;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.util.HashMap;

public enum LiveImage
{
    INSTANCE;

    private HashMap<ResourceLocation, MidiTexture> map = new HashMap<>();
    private TObjectLongHashMap<MidiTexture> activeItems = new TObjectLongHashMap<>();

    private long pass = 0;

    LiveImage()
    {
        MinecraftForge.EVENT_BUS.register(this);
        registerSong(new MidiTexture.EmptyPage());
    }

    public synchronized void registerSong(MidiTexture location)
    {
        map.put(location.resourceLocation(), location);
    }

    public synchronized MidiTexture getSong(ResourceLocation rl)
    {
        final MidiTexture texture = map.get(rl);

        if (texture == null)
            return getSong(ItemPianoRoll.NONE);

        if (!texture.hasTextureID())
        {
            try
            {
                texture.loadTexture(Minecraft.getMinecraft().getResourceManager());
            } catch (IOException e)
            {
            }
        }

        activeItems.put(texture, System.currentTimeMillis());

        return texture;
    }

    @SubscribeEvent
    public void removeOldTextures(TickEvent event)
    {
        if (event.type == TickEvent.Type.CLIENT && event.phase == TickEvent.Phase.START && pass < System.currentTimeMillis())
        {
            synchronized (this)
            {
                pass = System.currentTimeMillis() + Settings.PianoPlayer.ums_texture_poll_time;

                activeItems.retainEntries((tex, timeStamp) -> {
                    if (System.currentTimeMillis() > timeStamp + Settings.PianoPlayer.ums_texture_unload_time)
                    {
                        tex.deleteGlTexture();
                        return false;
                    }

                    return true;
                });
            }
        }
    }
}
