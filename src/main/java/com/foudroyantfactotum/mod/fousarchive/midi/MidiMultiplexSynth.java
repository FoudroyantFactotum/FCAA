package com.foudroyantfactotum.mod.fousarchive.midi;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum MidiMultiplexSynth
{
    INSTANCE;

    public static final byte channelsPerSynth = 10; //upto max of 16
    public static final byte additionalChannels = 5;
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

        final MultiplexMidiReceiver mmr = new MultiplexMidiReceiver(receivers.size());
        receivers.add(mmr);

        return mmr;
    }

    private synchronized void removeAllocatedChannel(int channel)
    {
        receivers.remove(channel);

        for (int cid=channel; cid < receivers.size(); ++cid)
        {
            final MultiplexMidiReceiver mmr = receivers.get(cid);

            synchronized (mmr)
            {
                mmr.config(mmr.channel-1);
            }

            //remove extra synths if threshold passes
            final int pns = (mmr.channel+additionalChannels) / channelsPerSynth;
            if (mmr.synthID != pns)
            {
                synths.remove(pns).close();
            }
        }
    }

    public class MultiplexMidiReceiver implements Receiver
    {
        private int channel;
        private int synthID;
        private int channelID;

        private MultiplexMidiReceiver(int channel)
        {
            config(channel);
        }

        void config(int channel)
        {
            this.channel = channel;
            this.synthID = channel/channelsPerSynth;
            this.channelID = channel%channelsPerSynth;
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

                synths.get(synthID).getReceiver().send(mm, l);
            }
            catch (MidiUnavailableException | InvalidMidiDataException e) { }
        }

        public synchronized void turnNotesOff()
        {
            synths.get(synthID).getChannels()[channelID].allNotesOff();
        }

        public synchronized void changeVolumeLevel(int vol)
        {
            synths.get(synthID).getChannels()[channelID].controlChange(7, vol);
        }

        @Override
        public void close()
        {
            removeAllocatedChannel(channel);
        }
    }
}
