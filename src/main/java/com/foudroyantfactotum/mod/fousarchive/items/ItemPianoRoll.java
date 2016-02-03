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
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ItemPianoRoll extends FA_Item
{
    public static ItemPianoRoll INSTANCE = null;

    public static final String prefixLocation = "midi";
    public static final ResourceLocation masterListFile = new ResourceLocation(TheMod.MOD_ID, prefixLocation + "/master");
    private static final List<ResourceLocation> allSongs = new ArrayList<>();
    private static final List<MidiDetails> songDetails = new ArrayList<>();

    public static void init()
    {
        try(final InputStream stream = Minecraft.getMinecraft().getResourceManager()
                .getResource(masterListFile)
                .getInputStream())
        {
            final Scanner scanner = new Scanner(stream).useDelimiter("\n");
            while (scanner.hasNext())
            {
                loadSongsFromSubdirectory(prefixLocation + '/' + scanner.next());
            }
        } catch (IOException e)
        {
            throw new FousArchiveException("Missing master Midi file list");
        }

        allSongs.stream().map((rl) -> {
            try (final InputStream stream = Minecraft.getMinecraft().getResourceManager()
                    .getResource(rl)
                    .getInputStream())
            {
                return MidiDetails.getMidiDetails(stream);
            } catch (IOException e)
            {
                throw new FousArchiveException(e);
            }
        }).forEach(songDetails::add);
    }

    private static void loadSongsFromSubdirectory(String dir)
    {
        try(final Scanner scanner = new Scanner(Minecraft.getMinecraft().getResourceManager()
                .getResource(new ResourceLocation(TheMod.MOD_ID,dir + "/master"))
                .getInputStream()).useDelimiter("\n"))
        {
            while (scanner.hasNext())
            {
                final ResourceLocation rl = new ResourceLocation(TheMod.MOD_ID, dir + "/" + scanner.next());

                LiveImage.INSTANCE.registerSong(new MidiTexture(rl));
                ItemPianoRoll.addPianoRoll(rl);
            }
        } catch (IOException e)
        {
            throw new FousArchiveException("Missing master list for " + dir, e);
        }
    }

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
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }

    @Override
    public int getMaxDamage()
    {
        return allSongs.size();
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

        final int dam = stack.getItemDamage();

        if (dam > -1 && dam < allSongs.size())
        {
            final MidiDetails song = songDetails.get(dam);
            if (song == MidiDetails.NO_DETAILS)
            {
                tooltip.add(getPianoRoll(dam).toString());
            } else {
                tooltip.add(song.getSimpleDetails());
                if (advanced)
                {
                    for (final String detailLine : song.getLongDetails().split("\n"))
                        tooltip.add(detailLine);
                }
            }
        } else {
            tooltip.add("INVALID");
        }
    }

    @Override
    public String getHighlightTip(ItemStack item, String displayName)
    {
        final int dam = item.getItemDamage();

        if (dam > -1 && dam < allSongs.size())
        {
            final MidiDetails song = songDetails.get(dam);

            if (song == MidiDetails.NO_DETAILS)
            {
                return getPianoRoll(dam).toString();
            } else {
                return song.getSimpleDetails();
            }
        } else {
            return "INVALID + " + displayName;
        }
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

            for (int i = 0; i < songDetails.size(); ++i)
            {
                final MidiDetails song = songDetails.get(i);

                if (song.title != null && song.title.startsWith(partialName))
                    sender.addChatMessage(new ChatComponentText("("+i+")"+song.getSimpleDetails()));
            }
        }

        private String getAsSingleString(String[] str)
        {
            if (str == null || str.length < 1)
                return "";

            final StringBuilder builder = new StringBuilder(str.length);

            for (int i=0; i < str.length-1; ++i)
            {
                builder.append(str[i]);
                builder.append(' ');
            }

            builder.append(str[str.length-1]);

            return builder.toString();
        }
    }
}
