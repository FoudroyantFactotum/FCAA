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
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ItemPianoRoll extends FA_Item
{
    public static ItemPianoRoll INSTANCE = null;

    public static final ResourceLocation NONE = new ResourceLocation(TheMod.MOD_ID, "midi/NONE");
    public static final String ROLL = "pianoRoll";

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

    public ItemPianoRoll()
    {
        setUnlocalizedName("pianoRoll");
        setMaxStackSize(1);
        setNoRepair();
        LiveMidiDetails.INSTANCE.addSongDetails(NONE, MidiDetails.NO_DETAILS);
        LiveImage.INSTANCE.registerSong(new MidiTexture.EmptyPage());
    }

    @Override
    public boolean isDamageable()
    {
        return false;
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
            tooltip.add(MidiDetails.NO_DETAILS.getSimpleDetails());
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
            return MidiDetails.NO_DETAILS.getSimpleDetails();

        final ResourceLocation song = getTagResourceOrElse(stack.getTagCompound(), ROLL, NONE);
        final MidiDetails detail = LiveMidiDetails.INSTANCE.getDetailsOnSong(song);

        return detail.getSimpleDetails();
    }

    public static class CommandPianoRollID extends CommandBase
    {
        @Override
        public String getCommandName()
        {
            return "listPianoRollID";
        }

        @Override
        public String getCommandUsage(ICommandSender sender)
        {
            return "/listPianoRollID [full or partial name]";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException
        {
            final String partialName = getAsSingleString(args);
            final ResourceLocation[] songList = LiveMidiDetails.INSTANCE.songDetails();
            ResourceLocation rl = null;
            int count = 0;

            for (int i = 0; i < songList.length; ++i)
            {
                final MidiDetails song = LiveMidiDetails.INSTANCE.getDetailsOnSong(songList[i]);

                if (song.title != null && song.title.toLowerCase().startsWith(partialName.toLowerCase()))
                {
                    rl = songList[i];
                    ++count;
                    sender.addChatMessage(new ChatComponentText(song.getSimpleDetails()));
                }
            }

            if (count == 1 && rl != null)
            {
                final BlockPos sp = sender.getPosition();
                final ItemStack stack = new ItemStack(ItemPianoRoll.INSTANCE, 1);
                final NBTTagCompound nbt = new NBTTagCompound();

                nbt.setString(ItemPianoRoll.ROLL, rl.toString());
                stack.setTagCompound(nbt);

                sender.getEntityWorld().spawnEntityInWorld(new EntityItem(sender.getEntityWorld(), sp.getX(), sp.getY(), sp.getZ(), stack));
            }
        }

        private String getAsSingleString(String[] str)
        {
            if (str == null || str.length < 1)
                return "";

            final StringBuilder builder = new StringBuilder(str.length);

            for (int i = 0; i < str.length - 1; ++i)
            {
                builder.append(str[i]);
                builder.append(' ');
            }

            builder.append(str[str.length - 1]);

            return builder.toString();
        }
    }
}
