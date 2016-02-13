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
package com.foudroyantfactotum.mod.fousarchive.utility;

import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public final class Utility
{
    private Utility(){/*noop*/}

    public static void registerAdditionalPianoRoll(@Nonnull ResourceLocation rl, @Nonnull String name, @Nonnull String composer)
    {
        LiveImage.INSTANCE.registerSong(new MidiTexture(rl));
        ItemPianoRoll.addPianoRoll(rl);
        LiveMidiDetails.INSTANCE.addSongDetails(rl, MidiDetails.simpleMD(name, composer));
    }
}
