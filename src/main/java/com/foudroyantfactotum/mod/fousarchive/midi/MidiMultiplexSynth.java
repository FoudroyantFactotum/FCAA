package com.foudroyantfactotum.mod.fousarchive.midi;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum MidiMultiplexSynth
{
    INSTANCE;

    public static final byte channelsPerSynth = 9; //upto max of 16. channel 10 is always used for drums only.
    public static final byte additionalChannels = 3;
    private final List<Synthesizer> synths = new LinkedList<>();
    private final List<MultiplexMidiReceiver> receivers = new ArrayList<>();

    public synchronized MultiplexMidiReceiver getNewReceiver() throws MidiUnavailableException
    {
        final int synthID = receivers.size()/channelsPerSynth;

        if (synthID >= synths.size())
        {
            final Synthesizer synthesizer = MidiSystem.getSynthesizer();

            synthesizer.open();
            synths.add(synthesizer);
        }

        final MultiplexMidiReceiver mmr = new MultiplexMidiReceiver(receivers.size(), synths.get(receivers.size()/channelsPerSynth));
        receivers.add(mmr);

        return mmr;
    }

    private synchronized void removeAllocatedChannel(int channel)
    {
        receivers.remove(channel);
        synths.get(channel/channelsPerSynth).getChannels()[channel%channelsPerSynth].resetAllControllers();

        for (int cid=channel; cid < receivers.size(); ++cid)
        {
            final MultiplexMidiReceiver mmr = receivers.get(cid);

            mmr.config(mmr.channel-1, synths.get((mmr.channel-1)/channelsPerSynth));

        }

        //remove extra synths if threshold passes
        final int uSyntht = (receivers.size()+additionalChannels) / channelsPerSynth;
        final int uSynthb = (receivers.size()+additionalChannels-1) / channelsPerSynth;
        final int cSynth = receivers.size()/channelsPerSynth;
        if (uSyntht > cSynth && synths.size() > uSyntht && uSyntht != uSynthb)
        {
            synths.remove(uSyntht).close();
        }
    }

    public static class MultiplexMidiReceiver implements Receiver
    {
        private int channel;
        private int channelID;
        private Synthesizer synthesizer;

        private MultiplexMidiReceiver(int channel, Synthesizer synthesizer)
        {
            config(channel, synthesizer);
        }

        synchronized void config(int channel, Synthesizer synthesizer)
        {
            this.channel = channel;
            this.channelID = channel%channelsPerSynth;
            this.synthesizer = synthesizer;
        }

        @Override
        public synchronized void send(MidiMessage mm, long l)
        {
            try
            {
                if (mm instanceof ShortMessage)
                {
                    final ShortMessage sm = (ShortMessage) mm;

                    sm.setMessage(sm.getCommand(), channelID, sm.getData1(), sm.getData2());
                }

                synthesizer.getReceiver().send(mm, l);
            }
            catch (MidiUnavailableException | InvalidMidiDataException e) { }
        }

        public synchronized void turnNotesOff()
        {
            synthesizer.getChannels()[channelID].allNotesOff();
        }

        public synchronized void changeVolumeLevel(int vol)
        {
            synthesizer.getChannels()[channelID].controlChange(7, vol);
        }

        @Override
        public void close()
        {
            MidiMultiplexSynth.INSTANCE.removeAllocatedChannel(channel);
        }
    }
}
