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
package com.foudroyantfactotum.mod.fousarchive.midi;

import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Set;

public enum LiveMidiDetails
{
    INSTANCE;

    private HashMap<ResourceLocation, MidiDetails> details = new HashMap<>();

    @Nonnull
    public synchronized MidiDetails getDetailsOnSong(ResourceLocation rl)
    {
        if (details.containsKey(rl))
        {
            return details.get(rl);
        }

        return MidiDetails.NO_DETAILS;
    }

    public synchronized void addSongDetails(ResourceLocation rl, MidiDetails midiDetails)
    {
        if (midiDetails == null || rl == null)
            throw new FousArchiveException("Can not add midiDetails for : " + rl + " : " + midiDetails);

        details.put(rl, midiDetails);
    }

    public synchronized ResourceLocation[] songDetails()
    {
        final Set<ResourceLocation> keySet = details.keySet();

        return keySet.toArray(new ResourceLocation[keySet.size()]);
    }
}
