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

import com.foudroyantfactotum.mod.fousarchive.midi.midiPlayer.MidiPianoPlayer;
import com.foudroyantfactotum.mod.fousarchive.midi.state.SongPlayingState;
import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import com.foudroyantfactotum.tool.structure.registry.StructureDefinition;
import com.foudroyantfactotum.tool.structure.tileentity.StructureTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TEPlayerPiano extends StructureTE
{
    public static final ExecutorService midiService = Executors.newCachedThreadPool();
    public static final String ITEM_LOADED_SONG = "itemPianoRoll";
    public static final String SONG_POSITION = "songPos";
    public static final String SONG_STATE = "songState";

    @SideOnly(Side.CLIENT)
    public volatile float[] keyOffset;
    @SideOnly(Side.CLIENT)
    public volatile boolean[] keyIsDown;

    public volatile double songPos;
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
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setString(ITEM_LOADED_SONG, loadedSong == null ? "" : loadedSong.toString());
        nbt.setDouble(SONG_POSITION, songPos);
        nbt.setByte(SONG_STATE, (byte) songState.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        final String resName = nbt.getString(ITEM_LOADED_SONG);
        loadedSong = resName == null || resName.isEmpty() ? null : new ResourceLocation(resName);
        songPos = nbt.getDouble(SONG_POSITION);
        songState = SongPlayingState.values()[nbt.getByte(SONG_STATE)];

        if (worldObj != null && worldObj.isRemote)
        {
            configureMusicState();
            Logger.info("RF_NBT side : " + worldObj.isRemote);
        } else {
            Logger.info("asdfasdfasdfasdf");
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
            midiService.execute(new MidiPianoPlayer(this, songPos));
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
        if (worldObj != null && !worldObj.isRemote)
        {
            configureMusicState();
        }
    }

    @Override
    public String toString()
    {
        return "te.state: " + songState + " : " + songPos + " : " + loadedSong;
    }
}
