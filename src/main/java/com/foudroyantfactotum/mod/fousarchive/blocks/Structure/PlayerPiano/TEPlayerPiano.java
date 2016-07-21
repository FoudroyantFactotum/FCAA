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
package com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano;

import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.midiPlayer.MidiPianoPlayer;
import com.foudroyantfactotum.mod.fousarchive.midi.state.SongPlayingState;
import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import com.foudroyantfactotum.mod.fousarchive.utility.log.UserLogger;
import com.foudroyantfactotum.tool.structure.registry.StructureDefinition;
import com.foudroyantfactotum.tool.structure.tileentity.StructureTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TEPlayerPiano extends StructureTE
{
    public static final ExecutorService midiService = Executors.newCachedThreadPool();
    public static final Map<BlockPos, MidiPianoPlayer> existingPlayers_client = new HashMap<>();
    public static final Map<BlockPos, MidiPianoPlayer> existingPlayers_server = new HashMap<>();
    public static final String ITEM_LOADED_SONG = "itemPianoRoll";
    public static final String SONG_POSITION = "rollDisplayPosition";
    public static final String SONG_STATE = "songState";

    @SideOnly(Side.CLIENT)
    public volatile float[] keyOffset;
    @SideOnly(Side.CLIENT)
    public volatile boolean[] keyIsDown;
    @SideOnly(Side.CLIENT)
    public volatile double rollDisplayPosition;

    public volatile long songPosition;
    public volatile SongPlayingState songState = SongPlayingState.TERMINATED;

    public ResourceLocation loadedSong = null;

    public TEPlayerPiano()
    {
        initClient();
    }

    public TEPlayerPiano(StructureDefinition sd, EnumFacing orientation)
    {
        super(sd, orientation, false);

        initClient();
    }

    private void initClient()
    {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            keyOffset = new float[88];
            keyIsDown = new boolean[88];
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setString(ITEM_LOADED_SONG, loadedSong == null ? "" : loadedSong.toString());
        nbt.setLong(SONG_POSITION, songPosition);
        nbt.setByte(SONG_STATE, (byte) songState.ordinal());

        Logger.info(UserLogger.MIDI_PIANO, "WriteToNBT: songPosition@"+songPosition + " : " + songState + " : " + FMLCommonHandler.instance().getEffectiveSide());

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if (!nbt.hasKey(ITEM_LOADED_SONG)) return;

        final String resName = nbt.getString(ITEM_LOADED_SONG);
        loadedSong = resName.isEmpty() ? null : new ResourceLocation(resName);
        songPosition = nbt.getLong(SONG_POSITION);
        songState = SongPlayingState.values()[nbt.getByte(SONG_STATE)];

        final MidiDetails md = LiveMidiDetails.INSTANCE.getDetailsOnSong(loadedSong);
        if (md != MidiDetails.NO_DETAILS && FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            rollDisplayPosition = (double) songPosition / md.getMaxTicks();
        }

        Logger.info(UserLogger.MIDI_PIANO, "ReadFromNBT: songPosition@"+songPosition + " : " + songState + " : " + FMLCommonHandler.instance().getEffectiveSide());

        if (worldObj != null)
        {
            configureMusicState();
        }
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
    }

    private void configureMusicState()
    {
        if (songState != SongPlayingState.TERMINATED)
        {
            final Map<BlockPos, MidiPianoPlayer> existingPlayers = worldObj.isRemote ? existingPlayers_client : existingPlayers_server;
            final MidiPianoPlayer possiblePlayer = existingPlayers.get(this.getPos());
            Logger.info(UserLogger.MIDI_PIANO, "existing Players: " + existingPlayers);

            if (possiblePlayer == null || possiblePlayer.isPlayerDead())
            {
                if (possiblePlayer != null)
                    existingPlayers.remove(getPos());

                MidiPianoPlayer player = new MidiPianoPlayer(this, songPosition, FMLCommonHandler.instance().getEffectiveSide());

                Logger.info(UserLogger.MIDI_PIANO, "creating new PlayerPiano at " + getPos());
                Logger.info(UserLogger.MIDI_PIANO, "This side is running midiplayer " + FMLCommonHandler.instance().getEffectiveSide() + " : " + FMLCommonHandler.instance().getSide() + " : " + getWorld().isRemote);
                existingPlayers.put(getPos(), player);
                midiService.execute(player);
            }
        }
    }

    @Override
    public void onChunkUnload()
    {
        invalidate();
        super.onChunkUnload();
    }

    @Override
    public void onLoad()
    {
        if (worldObj != null)
        {
            configureMusicState();
        }
    }

    @Override
    public String toString()
    {
        return "te.state: " + songState + " : " + rollDisplayPosition + " : " + loadedSong;
    }
}
