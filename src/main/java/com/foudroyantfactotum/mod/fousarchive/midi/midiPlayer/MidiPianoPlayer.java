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

import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.TEPlayerPiano;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiMultiplexSynth;
import com.sun.media.sound.RealTimeSequencerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.sound.midi.*;
import java.io.IOException;
import java.io.InputStream;

public class MidiPianoPlayer implements Runnable
{
    private final TEPlayerPiano te;
    private final double startPos;

    public MidiPianoPlayer(TEPlayerPiano te, double startPos)
    {
        this.te = te;
        this.startPos = startPos;
    }

    @SideOnly(Side.CLIENT)
    public void playClient() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException
    {
        if (te.isInvalid() || te.loadedSong == -1 || ItemPianoRoll.getPianoRoll(te.loadedSong) == null)
            return;

        final InputStream midiStream;
        final Sequencer sequencer;
        final MidiMultiplexSynth.MultiplexMidiReceiver receiver;

        synchronized (MidiSystem.class)
        {
            midiStream = Minecraft.getMinecraft().getResourceManager().getResource(ItemPianoRoll.getPianoRoll(te.loadedSong)).getInputStream();
            sequencer = (Sequencer) new RealTimeSequencerProvider().getDevice(null);
            receiver = MidiMultiplexSynth.INSTANCE.getNewReceiver();
        }

        sequencer.open();
        sequencer.setSequence(midiStream);

        sequencer.getTransmitter().setReceiver(new EventSieve(receiver));

        sequencer.setTickPosition((long) (startPos * sequencer.getTickLength()));
        sequencer.start();
        {
            try
            {
                while (sequencer.isRunning() && !te.isInvalid() && Minecraft.getMinecraft().thePlayer != null)
                {
                    if (!te.isSongRunning && allKeysInRightPosition(te.keyOffset))
                    {
                        te.hasSongTerminated = true;
                        sequencer.stop();
                        continue;
                    }

                    if (!te.isSongPlaying && te.isSongRunning)
                    {
                        for (int key = 0; key < te.keyIsDown.length; ++key)
                        {
                            if (te.keyIsDown[key])
                            {
                                receiver.turnNotesOff();
                                te.keyIsDown[key] = false;
                            }
                        }

                        te.isSongRunning = false;
                    }

                    for (int i = 0; i < te.keyOffset.length; ++i)
                    {
                        if (!te.keyIsDown[i])
                        {
                            if (te.keyOffset[i] < 0)
                                te.keyOffset[i] += 0.0005;
                            else
                                te.keyOffset[i] = 0;
                        }
                    }

                    //change audio level for player
                    final int audioLevel;

                    if (Minecraft.getMinecraft().thePlayer != null)
                    {
                        final BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                        final double distance = playerPos.distanceSq(te.getPos());

                        if (distance > 35)
                        {
                            audioLevel = (int) (Math.abs(1 - Math.min((distance-35)/800, 1.0)) * 127);
                        } else {
                            audioLevel = 127;
                        }

                    } else {
                        audioLevel = 0;
                    }

                    receiver.changeVolumeLevel(audioLevel);

                    te.songPos = (double) sequencer.getTickPosition() / sequencer.getTickLength();

                    Thread.sleep(2);
                }
            } catch (NullPointerException | ClassCastException e)
            {
                e.printStackTrace();
                sequencer.stop();
            }
        }
        te.isSongPlaying = false;
        te.isSongRunning = false;
        te.hasSongTerminated = true;
        sequencer.stop();
        receiver.close();

        if (te.songPos == 1.0)
        {
            te.songPos = 0.0;
        }
    }

    private void playServer() throws IOException, MidiUnavailableException, InvalidMidiDataException, InterruptedException
    {
        if (te.isInvalid() || te.loadedSong == -1)
            return;

        final InputStream midiStream;
        final Sequencer sequencer;

        synchronized (MidiSystem.class)
        {
            midiStream = Minecraft.getMinecraft().getResourceManager().getResource(ItemPianoRoll.getPianoRoll(te.loadedSong)).getInputStream();
            sequencer = (Sequencer) new RealTimeSequencerProvider().getDevice(null);
        }

        sequencer.open();
        sequencer.setSequence(midiStream);

        sequencer.setTickPosition((long) (startPos * sequencer.getTickLength()));
        sequencer.start();
        {
            try
            {
                while (sequencer.isRunning() && !te.isInvalid())
                {
                    if (!te.isSongRunning && allKeysInRightPosition(te.keyOffset))
                    {
                        te.hasSongTerminated = true;
                        sequencer.stop();
                        continue;
                    }

                    if (!te.isSongPlaying && te.isSongRunning)
                    {
                        te.isSongRunning = false;
                    }

                    te.songPos = (double) sequencer.getTickPosition() / sequencer.getTickLength();

                    Thread.sleep(1);
                }
            } catch (NullPointerException | ClassCastException e)
            {
                e.printStackTrace();
                sequencer.stop();
            }
        }
        te.isSongPlaying = false;
        te.isSongRunning = false;
        te.hasSongTerminated = true;
        sequencer.stop();

        if (te.songPos == 1.0)
        {
            te.songPos = 0.0;
        }
        te.markDirty();
    }

    private boolean allKeysInRightPosition(float[] keyOffset)
    {
        for (float f : keyOffset)
            if (f < 0)
                return false;

        return true;
    }

    @Override
    public void run()
    {
        try
        {
            if (te.getWorld().isRemote)
                playClient();
            else
                playServer();
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
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
                if (te.isSongPlaying)
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

                    if (sm.getData1() - 21 > -1 && sm.getData1() - 21 < te.keyOffset.length)
                    {
                        te.keyIsDown[sm.getData1() - 21] = sm.getData2() != 0;

                        if (sm.getData2() != 0)
                            te.keyOffset[sm.getData1() - 21] = -0.03f;
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
