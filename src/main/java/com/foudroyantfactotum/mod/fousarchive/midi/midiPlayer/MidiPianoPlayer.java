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
package com.foudroyantfactotum.mod.fousarchive.midi.midiPlayer;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.TEPlayerPiano;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiMultiplexSynth;
import com.foudroyantfactotum.mod.fousarchive.midi.state.SongPlayingState;
import com.foudroyantfactotum.mod.fousarchive.utility.Settings;
import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import com.foudroyantfactotum.mod.fousarchive.utility.log.UserLogger;
import com.sun.media.sound.RealTimeSequencerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.sound.midi.*;
import java.io.IOException;
import java.io.InputStream;

public class MidiPianoPlayer implements Runnable
{
    private TEPlayerPiano te;
    private final long startPosition;
    private boolean deathState = false;
    private Side side;

    public MidiPianoPlayer(TEPlayerPiano te, long startPosition, Side side)
    {
        this.te = te;
        this.startPosition = startPosition;
        this.side = side;
    }

    @SideOnly(Side.CLIENT)
    public void playClient() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException
    {
        final InputStream midiStream;
        final Sequencer sequencer;
        final MidiMultiplexSynth.MultiplexMidiReceiver receiver;

        synchronized (MidiSystem.class)
        {
            midiStream = Minecraft.getMinecraft().getResourceManager().getResource(te.loadedSong).getInputStream();
            sequencer = (Sequencer) new RealTimeSequencerProvider().getDevice(null);
        }
        receiver = MidiMultiplexSynth.INSTANCE.getNewReceiver();

        sequencer.open();
        sequencer.setSequence(midiStream);

        sequencer.getTransmitter().setReceiver(new EventSieve(receiver));

        sequencer.setTickPosition(startPosition);
        sequencer.start();
        {
            try
            {
                while (!te.isInvalid() && Minecraft.getMinecraft().thePlayer != null && te.songState != SongPlayingState.TERMINATED)
                {
                    if (!sequencer.isRunning())
                    {
                        Logger.info(UserLogger.MIDI_PIANO, "Song has finished -> RUNNING " + side);
                        te.songState = SongPlayingState.RUNNING;
                    }
                    //terminate player once keys are in the restored position
                    if (te.songState == SongPlayingState.RUNNING && allKeysInRightPosition(te.keyOffset))
                    {
                        Logger.info(UserLogger.MIDI_PIANO, "All keys in right position-> TERMINATED " + side);
                        te.songState = SongPlayingState.TERMINATED;
                        continue;
                    }

                    //restore key positions before the player stop
                    if (te.songState == SongPlayingState.RUNNING)
                    {
                        receiver.turnNotesOff();
                        for (int key = 0; key < te.keyIsDown.length; ++key)
                        {
                            if (te.keyIsDown[key])
                            {
                                te.keyIsDown[key] = false;
                            }
                        }
                    }

                    for (int i = 0; i < te.keyOffset.length; ++i)
                    {
                        if (!te.keyIsDown[i])
                        {
                            if (te.keyOffset[i] < 0)
                                te.keyOffset[i] += Settings.PianoPlayer.d_key_restore_time;
                            else
                                te.keyOffset[i] = 0;
                        }
                    }

                    //change audio level for player
                    final int audioLevel;

                    if (Minecraft.getMinecraft().thePlayer != null)
                    {
                        if (sequencer.getTickPosition() % 4 == 0)
                        {
                            final BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                            final double distance = playerPos.distanceSq(te.getPos());

                            if (distance > Settings.PianoPlayer.i_start_position_audio_fall_off)
                            {
                                audioLevel = (int) ((
                                        Math.abs(1 - Math.min((distance - Settings.PianoPlayer.i_start_position_audio_fall_off) / 800, 1.0))
                                                * Settings.PianoPlayer.b7_max_vol) *
                                        Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS)
                                );
                            } else
                            {
                                audioLevel = (int) (Settings.PianoPlayer.b7_max_vol * Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS));
                            }
                        } else {
                            audioLevel = receiver.getVolumeLevel();
                        }
                    } else {
                        audioLevel = 0;
                    }

                    receiver.changeVolumeLevel(audioLevel);

                    te.songPosition = sequencer.getTickPosition();
                    te.rollDisplayPosition = (double) sequencer.getTickPosition() / sequencer.getTickLength();

                    Thread.sleep(4);
                }
            } catch (NullPointerException | ClassCastException e)
            {
                e.printStackTrace();
                sequencer.stop();
            }
        }
        sequencer.stop();
        receiver.close();

        Logger.info(UserLogger.MIDI_PIANO, "midiDest: " + sequencer.getTickPosition() + " : " + sequencer.getTickLength() + " : " + te.songState + " : " + side);
        if (sequencer.getTickPosition() == sequencer.getTickLength())
        {
            te.rollDisplayPosition = 0.0;
            te.songPosition = 0;
        }
    }

    private void playServer() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException
    {
        if (te.songState == SongPlayingState.RUNNING)
        {
            Logger.info(UserLogger.MIDI_PIANO, "midi loaded with RUNNING -> TERMINATED");
            te.songState = SongPlayingState.TERMINATED;
            te.markDirty();
            return;
        }

        final InputStream midiStream;
        final Sequencer sequencer;
        long transitionTime = -1;

        synchronized (MidiSystem.class)
        {
            midiStream = TheMod.proxy.getInputStream(te.loadedSong);
            sequencer = (Sequencer) new RealTimeSequencerProvider().getDevice(null);
        }

        sequencer.open();
        sequencer.setSequence(midiStream);

        sequencer.setTickPosition(startPosition);
        sequencer.start();
        {
            try
            {
                while (!te.isInvalid() && te.songState != SongPlayingState.TERMINATED && isServerAlive(te))
                {
                    if (!sequencer.isRunning() && te.songState == SongPlayingState.PLAYING)
                    {
                        Logger.info(UserLogger.MIDI_PIANO, "Song has finished -> RUNNING " + side);
                        te.songState = SongPlayingState.RUNNING;
                    }

                    if (te.songState == SongPlayingState.RUNNING)
                    {
                        if (transitionTime != -1)
                        {
                            if (transitionTime < System.currentTimeMillis())
                            {
                                Logger.info(UserLogger.MIDI_PIANO, "Key Delay Done-> TERMINATED " + side);
                                te.songState = SongPlayingState.TERMINATED;
                            }
                        } else {
                            transitionTime = System.currentTimeMillis() + (long)(0.03/Settings.PianoPlayer.d_key_restore_time*4);
                        }

                        continue;
                    }

                    te.songPosition = sequencer.getTickPosition();

                    Thread.sleep(4);
                    te.markDirty();
                }
            } catch (NullPointerException | ClassCastException e)
            {
                e.printStackTrace();
                sequencer.stop();
            }
        }
        sequencer.stop();

        Logger.info(UserLogger.MIDI_PIANO, "midiDest: " + sequencer.getTickPosition() + " : " + sequencer.getTickLength() + " : " + te.songState + " : " + side);

        if (sequencer.getTickPosition() == sequencer.getTickLength())
        {
            te.songPosition = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean allKeysInRightPosition(float[] keyOffset)
    {
        for (float f : keyOffset)
            if (f < 0)
                return false;

        return true;
    }

    private static boolean isServerAlive(TileEntity te)
    {
        if (te.getWorld() != null)
        {
            if (te.getWorld().getMinecraftServer() != null)
            {
                return te.getWorld().getMinecraftServer().isServerRunning();
            }
        }

        return false;
    }

    @Override
    public void run()
    {
        if (!(te.isInvalid() || te.loadedSong == null || te.loadedSong.getResourcePath().equals(ItemPianoRoll.NONE.getResourcePath())))
        {
            try
            {
                if (te.getWorld().isRemote)
                    playClient();
                else
                    playServer();
            } catch (MidiUnavailableException | InvalidMidiDataException | IOException | InterruptedException e)
            {
                te.songState = SongPlayingState.TERMINATED;
                e.printStackTrace();
            }

            if (side == Side.CLIENT)
            {
                TEPlayerPiano.existingPlayers_client.remove(te.getPos());
            } else
            {
                TEPlayerPiano.existingPlayers_server.remove(te.getPos());
            }
        }

        te.markDirty();
        te = null;

        deathState = true;
    }

    public boolean isPlayerDead()
    {
        return deathState;
    }

    private class EventSieve implements Receiver
    {
        private Receiver rec;

        public EventSieve(Receiver rec)
        {
            this.rec = rec;
        }

        @Override
        public void send(MidiMessage midiMessage, long l)
        {
            try
            {
                if (te.songState == SongPlayingState.PLAYING)
                {
                    rec.send(midiMessage, 0);
                } else
                    return;

                if (midiMessage instanceof ShortMessage)
                {
                    final ShortMessage sm = (ShortMessage) midiMessage;

                    /*switch (sm.getCommand())
                    {
                        case ShortMessage.MIDI_TIME_CODE: Logger.info("MIDI_TIME_CODE"); break;
                        case ShortMessage.SONG_POSITION_POINTER: Logger.info("SONG_POSITION_POINTER"); break;
                        case ShortMessage.SONG_SELECT: Logger.info("SONG_SELECT");break;
                        case ShortMessage.TUNE_REQUEST: Logger.info("TUNE_REQUEST");break;
                        case ShortMessage.END_OF_EXCLUSIVE: Logger.info("END_OF_EXCLUSIVE");break;
                        case ShortMessage.TIMING_CLOCK: Logger.info("TIME_CLOCK");break;
                        case ShortMessage.START: Logger.info("START");break;
                        case ShortMessage.CONTINUE: Logger.info("CONTINUE");break;
                        case ShortMessage.STOP: Logger.info("STOP");break;
                        case ShortMessage.ACTIVE_SENSING: Logger.info("ACTIVE_SENSING");break;
                        case ShortMessage.SYSTEM_RESET: Logger.info("SYSTEM_RESET");break;
                        case ShortMessage.NOTE_OFF: Logger.info("NOTE_OFF");break;
                        case ShortMessage.NOTE_ON: Logger.info("NOTE_ON");break;
                        case ShortMessage.POLY_PRESSURE: Logger.info("POLY_PRESSURE");break;
                        case ShortMessage.CONTROL_CHANGE: Logger.info("CONTROL_CHANGE " + sm.getData1() + " : " + sm.getData2());break;
                        case ShortMessage.PROGRAM_CHANGE: Logger.info("PROGRAM_CHANGE");break;
                        case ShortMessage.CHANNEL_PRESSURE: Logger.info("CHANNEL_PRESSURE"); break;
                        case ShortMessage.PITCH_BEND: Logger.info("PITCH_BEND");break;
                    }*/

                    if (sm.getData1() - 18 > -1 && sm.getData1() - 18 < te.keyOffset.length)
                    {
                        te.keyIsDown[sm.getData1() - 18] = sm.getData2() != 0;

                        if (sm.getData2() != 0)
                            te.keyOffset[sm.getData1() - 18] = -0.03f;
                    }
                }

            } catch (NullPointerException | ClassCastException e)
            {

            }
        }

        @Override
        public void close()
        {
            rec.close();
        }
    }
}
