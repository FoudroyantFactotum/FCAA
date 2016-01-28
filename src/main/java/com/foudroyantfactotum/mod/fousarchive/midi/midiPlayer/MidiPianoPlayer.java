package com.foudroyantfactotum.mod.fousarchive.midi.midiPlayer;

import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.TEPlayerPiano;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiMultiplexSynth;
import com.foudroyantfactotum.mod.fousarchive.utility.Log.Logger;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.sound.midi.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
        if (te.isInvalid())
            return;

        final InputStream midiStream;
        final Sequencer sequencer;
        final MidiMultiplexSynth.MultiplexMidiReceiver receiver;

        synchronized (MidiSystem.class)
        {
            midiStream = Minecraft.getMinecraft().getResourceManager().getResource(te.loadedSong.getSongResource()).getInputStream();
            sequencer = MidiSystem.getSequencer();
            receiver = MidiMultiplexSynth.INSTANCE.getNewReceiver();
        }

        sequencer.open();
        sequencer.setSequence(midiStream);

        //why is the sequencer already hooked into a synth. gah
        sequencer.getTransmitters().get(0).setReceiver(new EventSieve(receiver));

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

                    final int playerDistance =
                            (int) (Math.abs(Minecraft.getMinecraft().thePlayer.getPosition().distanceSq(te.getPos()) / 300 - 1) * 127);

                    receiver.changeVolumeLevel(playerDistance);

                    te.songPos = (double) sequencer.getTickPosition() / sequencer.getTickLength();

                    //Logger.info("Client: " + te.toString() + " : " + Arrays.toString(te.keyOffset));

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

        Logger.info("Client: last: " + te.toString() + " : " + Arrays.toString(te.keyOffset));
    }

    private void playServer() throws IOException, MidiUnavailableException, InvalidMidiDataException, InterruptedException
    {
        if (te.isInvalid())
            return;

        final InputStream midiStream;
        final Sequencer sequencer;

        synchronized (MidiSystem.class)
        {
            midiStream = Minecraft.getMinecraft().getResourceManager().getResource(te.loadedSong.getSongResource()).getInputStream();
            sequencer = MidiSystem.getSequencer();
        }

        sequencer.open();
        sequencer.setSequence(midiStream);

        //why is the sequencer already hooked into a synth. gah
        sequencer.getTransmitters().get(0).setReceiver(new NullSieve());

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

                    //Logger.info("Server: " + te);

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

        Logger.info("Server: last: " + te);
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
                    rec.send(midiMessage, l);
                } else
                    return;

                if (midiMessage instanceof ShortMessage)
                {
                    final ShortMessage sm = (ShortMessage) midiMessage;

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

    private class NullSieve implements Receiver
    {
        @Override
        public void send(MidiMessage midiMessage, long l)
        {
            //noop
        }

        @Override
        public void close()
        {
            //noop
        }
    }
}
