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
package com.foudroyantfactotum.mod.fousarchive.items;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ItemPianoRoll extends FA_Item
{
    public static final ResourceLocation NONE = new ResourceLocation(TheMod.MOD_ID, "midi/NONE");
    public static final String ROLL = "pianoRoll";
    public static final String COLOUR = "rollColour";

    public static final int colourOffset = 0x111111;
    public static final int colourRange = 0xDDDDDD;
    public static final int iconNo = 9;

    private static final List<ResourceLocation> allSongs = new ArrayList<>();

    public static void addPianoRoll(ResourceLocation rl)
    {
        allSongs.add(rl);
    }

    public static ResourceLocation getPianoRoll(int index)
    {
        if (index > -1 && index < allSongs.size())
            return allSongs.get(index);

        return null;
    }

    public static int getPianoRollCount()
    {
        return allSongs.size();
    }

    public ItemPianoRoll()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
        LiveMidiDetails.INSTANCE.addSongDetails(NONE, MidiDetails.NO_DETAILS);
    }

    public static int getColourOffset()
    {
        return colourOffset;
    }

    @Override
    public int getMetadata(int damage)
    {
        if (damage > -1 && damage < iconNo)
        {
            return damage;
        }

        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }

    @Override
    public boolean isItemTool(ItemStack stack)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        tooltip.clear();

        if (stack.getTagCompound() == null)
        {
            tooltip.add("Blank Roll");
            return;
        }

        final ResourceLocation song = getTagResourceOrElse(stack.getTagCompound(), ROLL, NONE);
        final MidiDetails detail = LiveMidiDetails.INSTANCE.getDetailsOnSong(song);

        tooltip.add(detail.getSimpleDetails());

        if (detail != MidiDetails.NO_DETAILS)
        {
            if (advanced || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            {
                for (final String detailLine : detail.getLongDetails().split("\n"))
                    tooltip.add(detailLine);
            }
        }
    }

    private static ResourceLocation getTagResourceOrElse(NBTTagCompound nbt, String tag, ResourceLocation orElse)
    {
        return nbt.hasKey(tag) ? new ResourceLocation(nbt.getString(tag)) : orElse;
    }

    @Override
    public String getHighlightTip(ItemStack stack, String displayName)
    {
        if (stack.getTagCompound() == null)
            return "Blank Roll";

        final ResourceLocation song = getTagResourceOrElse(stack.getTagCompound(), ROLL, NONE);
        final MidiDetails detail = LiveMidiDetails.INSTANCE.getDetailsOnSong(song);

        return detail.getSimpleDetails();
    }

    public static void setPianoRollNBT(NBTTagCompound nbt, String name)
    {
        nbt.setString(ROLL, name);
        nbt.setInteger(COLOUR, (name.hashCode() % colourRange) + colourOffset);
    }

}
