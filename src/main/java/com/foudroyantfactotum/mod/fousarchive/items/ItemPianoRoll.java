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
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Instance;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Item;
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
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Auto_Item(name = "pianoRoll")
public class ItemPianoRoll extends FA_Item
{
    @Auto_Instance
    public static final ItemPianoRoll INSTANCE = null;

    public static final ResourceLocation NONE = new ResourceLocation(TheMod.MOD_ID, "midi/NONE");
    public static final String ROLL = "pianoRoll";
    public static final String COLOUR = "rollColour";

    public static final int colourOffset = 0x111111;
    public static final int colourRange = 0xDDDDDD;
    public static final int iconNo = 3;

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
        LiveImage.INSTANCE.registerSong(new MidiTexture.EmptyPage());
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass == 1)
        {
            final NBTTagCompound nbt = stack.getTagCompound();

            if (nbt != null && nbt.hasKey(COLOUR))
                return nbt.getInteger(COLOUR);
        }

        return 0xFFFFFF;
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

    public static void setPianoRollNBT(NBTTagCompound nbt, String name)
    {
        nbt.setString(ROLL, name);
        nbt.setInteger(COLOUR, (name.hashCode() % colourRange) + colourOffset);
    }

    public static class CommandPianoRollID extends CommandBase
    {
        @Override
        public String getCommandName()
        {
            return "listPianoRolls";
        }

        @Override
        public String getCommandUsage(ICommandSender sender)
        {
            return "/listPianoRolls [t\"Song Title\"] [c\"Song Author\"] [d\"Song Year\"] [m\"Manufacturer\"]";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException
        {
            try
            {
                final List<Pair<SearchMatch, String>> pt = parse(getAsSingleString(args));
                if (pt.size() > 10)
                    throw new CommandException("Too many matches");

                final List<Predicate<MidiDetails>> func = new ArrayList<>(pt.size());
                final List<SearchMatch> outfString = new LinkedList<>();

                for (Pair<SearchMatch, String> p : pt)
                {
                    func.add(p.getLeft().checkDetails(p.getRight()));
                    outfString.add(p.getLeft());
                }

                final ResourceLocation[] songList = LiveMidiDetails.INSTANCE.songDetails();
                final List<Pair<ResourceLocation, MidiDetails>> validList = new LinkedList<>();

                sender.addChatMessage(new ChatComponentText("==========Searching=========="));

                    song:
                    for (int i = 0; i < songList.length; ++i)
                    {
                        final MidiDetails song = LiveMidiDetails.INSTANCE.getDetailsOnSong(songList[i]);

                        for (Predicate<MidiDetails> p : func)
                            if (!p.test(song))
                                continue song;

                        validList.add(Pair.of(songList[i], song));
                    }
                int i = 0;

                for (Pair<ResourceLocation, MidiDetails> m : validList)
                    sender.addChatMessage(new ChatComponentText((++i) + ". " + toStringer(m.getRight(), outfString)));

                sender.addChatMessage(new ChatComponentText("=========End  Search========="));

                if (validList.size() == 1)
                {
                    final BlockPos sp = sender.getPosition();
                    final ItemStack stack = new ItemStack(ItemPianoRoll.INSTANCE, 1, validList.get(0).getLeft().toString().hashCode() % iconNo);
                    final NBTTagCompound nbt = new NBTTagCompound();

                    setPianoRollNBT(nbt, validList.get(0).getLeft().toString());
                    stack.setTagCompound(nbt);

                    sender.getEntityWorld().spawnEntityInWorld(new EntityItem(sender.getEntityWorld(), sp.getX(), sp.getY(), sp.getZ(), stack));
                }
            } catch (Exception e)
            {
                if (e instanceof CommandException)
                    throw e;
            }
        }

        private static String toStringer(MidiDetails m, List<SearchMatch> v)
        {
            final StringBuilder sb = new StringBuilder(v.size()*2-1);
            int i = 0;

            for (SearchMatch f : v)
            {
                if (i++ > 0)
                    sb.append(" : ");

                sb.append(f.getValue(m));
            }

            return sb.toString();
        }

        private List<Pair<SearchMatch, String>> parse(String s) throws CommandException
        {
            final List<Pair<SearchMatch, String>> list = new LinkedList<>();

            while (!s.isEmpty() && s.length() >= 1)
            {
                final SearchMatch sm = SearchMatch.getOnChar(s.charAt(0));

                if (sm == null)
                    throw new CommandException("Expected SearchMatch value found '" + s.charAt(0) + '\'');

                if (s.length() <= 1)
                {
                    list.add(Pair.of(sm, ""));
                    s = "";
                    continue;
                }

                if (s.charAt(1) == '\"')
                {
                    s = s.substring(2);

                    final int ifq = s.indexOf('\"');
                    final String s1 = s.substring(0, ifq);

                    s = s.substring(ifq + 1).trim();

                    list.add(Pair.of(sm, s1));
                } else
                {
                    list.add(Pair.of(sm, ""));
                    s = s.substring(1).trim();
                }
            }

            return list;
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

        private enum SearchMatch
        {
            Title('t', MidiDetails::getTitle),
            Composer('c', MidiDetails::getComposer),
            Date('d', MidiDetails::getCompositionDate),
            Manufacturer('m', MidiDetails::getRollManufacturer);

            private char c;
            private Function<MidiDetails, String> func;

            SearchMatch(char c, Function<MidiDetails, String> func)
            {
                this.c = c;
                this.func = func;
            }

            public char getCommandChar()
            {
                return c;
            }

            public String getValue(MidiDetails m)
            {
                return func.apply(m);
            }

            public Predicate<MidiDetails> checkDetails(String s)
            {
                return (a) ->
                {
                    final String r = func.apply(a);

                    return r != null && r.toLowerCase().contains(s.replaceAll("\"", "").toLowerCase());
                };
            }

            public static SearchMatch getOnChar(char c)
            {
                for (SearchMatch s : SearchMatch.values())
                    if (s.c == c)
                        return s;

                return null;
            }
        }
    }
}
