package com.foudroyantfactotum.mod.fousarchive.command;

import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandPianoRollID extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "listPianoRolls";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/listPianoRolls [t\"Song Title\"] [c\"Song Composer\"] [d\"Song Year\"] [m\"Manufacturer\"]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        try
        {
            final List<Pair<SearchMatch, String>> pt = parse(getAsSingleString(args));
            if (pt.size() > 10)
                throw new CommandException("Too many matches");
            if (pt.size() == 0)
                pt.add(Pair.of(SearchMatch.Title, ""));

            final List<Predicate<MidiDetails>> func = new ArrayList<>(pt.size());
            final List<SearchMatch> outfString = new LinkedList<>();

            for (Pair<SearchMatch, String> p : pt)
            {
                func.add(p.getLeft().checkDetails(p.getRight()));
                outfString.add(p.getLeft());
            }

            final ResourceLocation[] songList = LiveMidiDetails.INSTANCE.songDetails();
            final List<Pair<ResourceLocation, MidiDetails>> validList = new LinkedList<>();

            sender.addChatMessage(new TextComponentString("==========Searching=========="));

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
                sender.addChatMessage(new TextComponentString("\u00A7l" + (++i) + ". \u00A7r\u00A7o" + toStringer(m.getRight(), outfString) + "\u00A7r"));

            sender.addChatMessage(new TextComponentString("=========End  Search========="));

            if (validList.size() == 1)
            {
                final BlockPos sp = sender.getPosition();
                final ItemStack stack = new ItemStack(ModItems.pianoRoll, 1, Math.abs(validList.get(0).getLeft().toString().hashCode()) % ItemPianoRoll.iconNo);
                final NBTTagCompound nbt = new NBTTagCompound();

                ItemPianoRoll.setPianoRollNBT(nbt, validList.get(0).getLeft().toString());
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

                if (ifq < 0)
                    throw new CommandException("Missing matching \" for '" + sm + '\'');

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
