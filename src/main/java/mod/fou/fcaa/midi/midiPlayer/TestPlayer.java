package mod.fou.fcaa.midi.midiPlayer;

import mod.fou.fcaa.TheMod;
import mod.fou.fcaa.blocks.Structure.PlayerPiano.TEPlayerPiano;
import mod.fou.fcaa.utility.Log.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import javax.sound.midi.*;
import java.io.IOException;
import java.io.InputStream;

public class TestPlayer implements Runnable
{
    private static final BlockPos bPos = new BlockPos(0,1,0);

    public void play() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException
    {
        Logger.info("Starting midi file");
        final InputStream midiStream = Minecraft.getMinecraft().getResourceManager().getResource(TheMod.midiFile).getInputStream();
        final Sequencer sequencer = MidiSystem.getSequencer();
        final Synthesizer synthesizer = MidiSystem.getSynthesizer();

        sequencer.open();
        sequencer.setSequence(midiStream);

        //why is the sequencer already hooked into a synth. gah
        sequencer.getTransmitters().get(0).setReceiver(new EventSieve(synthesizer.getReceiver()));

        synthesizer.open();
        sequencer.start();
        {
            try
            {
                while (sequencer.isRunning())
                {
                    final TEPlayerPiano te = (TEPlayerPiano) Minecraft.getMinecraft().thePlayer.getEntityWorld().getTileEntity(bPos);

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

                    te.songPos = (double) sequencer.getTickPosition() / sequencer.getTickLength();

                    final int playerDistance =
                            (int) (Math.abs(Minecraft.getMinecraft().thePlayer.getPosition().distanceSq(bPos)/300 -1)*127);

                    for (MidiChannel c : synthesizer.getChannels())
                        c.controlChange(7, playerDistance);

                    Thread.sleep(1);
                }
            } catch (NullPointerException | ClassCastException e)
            {
                e.printStackTrace();
                sequencer.stop();
            }
        }
        sequencer.stop();
        synthesizer.close();
    }

    @Override
    public void run()
    {
        try
        {
            play();
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
                rec.send(midiMessage, l);

                if (midiMessage instanceof ShortMessage)
                {
                    final TEPlayerPiano te = (TEPlayerPiano) Minecraft.getMinecraft().thePlayer.getEntityWorld().getTileEntity(bPos);
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
}
