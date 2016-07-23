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
package com.foudroyantfactotum.mod.fousarchive.library;

import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.items.ItemTuningFork;
import net.minecraft.item.Item;

public final class ModItems
{
    private ModItems()
    {
        //noop
    }

    public static final Item pianoRoll = new ItemPianoRoll().setUnlocalizedName(ModNames.Items.pianoRoll).setRegistryName(ModNames.Items.pianoRoll);
    public static final Item tuningFork = new ItemTuningFork().setUnlocalizedName(ModNames.Items.tuningFork).setRegistryName(ModNames.Items.tuningFork);
}
