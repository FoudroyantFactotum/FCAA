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
package mod.fou.fcaa.midi.generation;

import mod.fou.fcaa.utility.Log.Logger;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.*;

public class MidiImageGeneration
{
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private final BlockingQueue<Line> lines = new ArrayBlockingQueue<Line>(100);
    private static final Line TERMINATE = new Line(-1, -1.0, -1.0);

    private final int imgX;
    private final int imgY;
    private final Sequence sequencer;

    public MidiImageGeneration(InputStream addr, int imgX, int imgY) throws InvalidMidiDataException, IOException
    {
        sequencer = MidiSystem.getSequence(addr);

        this.imgX = imgX;
        this.imgY = imgY;
    }

    public ImmutablePair<String, BufferedImage> buildImage() throws InterruptedException, ExecutionException
    {
        final Future<String> name = pool.submit(new LineProcessor(imgX));
        final BufferedImage mdbf = new BufferedImage(imgX, imgY, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics g = mdbf.getGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, imgX, imgY);
        g.setColor(Color.WHITE);

        for (Line l = lines.take(); l != TERMINATE; l = lines.take())
        {
            g.drawLine(l.note, (int)Math.round(l.tickStart * imgY), l.note, (int) Math.round(l.tickEnd * imgY));
        }

        pool.shutdown();

        g.dispose();

        return ImmutablePair.of(name.get(), mdbf);
    }

    private class LineProcessor implements Callable<String>
    {
        private String songName = "NoName";
        private final int[] activeNote;

        LineProcessor(int trackedNotes)
        {
            activeNote = new int[trackedNotes];

            Arrays.fill(activeNote, -1);
        }

        @Override
        public String call() throws Exception
        {
            final Track[] midiTrack = sequencer.getTracks();

            for (int i = 0; i < midiTrack[0].size(); i++)
            {
                final MidiEvent event = midiTrack[0].get(i);
                final MidiMessage midimsg = event.getMessage();

                if (midimsg instanceof MetaMessage)
                {
                    final MetaMessage metamsg = (MetaMessage) midimsg;
                    final String data = new String(metamsg.getData());

                    if (data.startsWith("/title:"))
                    {
                        songName = data.substring(8, data.length() - 1);
                    }
                } else if (midimsg instanceof ShortMessage)
                {
                    final ShortMessage shortmsg = (ShortMessage) midimsg;
                    final int note = shortmsg.getData1();
                    final int vel = shortmsg.getData2();

                    if (note < activeNote.length)
                    {
                        if (vel == 0)
                        {
                            lines.put(new Line(
                                    note,
                                    activeNote[note] / (double) sequencer.getTickLength(),
                                    event.getTick() / (double) sequencer.getTickLength())
                            );

                            activeNote[note] = -1;
                        }
                        else if (activeNote[note] == -1)
                        {
                            activeNote[note] = (int) event.getTick();
                        }
                    }
                }
            }

            lines.put(TERMINATE);

            return songName;
        }
    }

    private static class Line
    {
        public final int note;
        public final double tickStart;
        public final double tickEnd;

        Line(int note, double tickStart, double tickEnd)
        {
            this.note = note;
            this.tickStart = tickStart;
            this.tickEnd = tickEnd;
        }

        @Override
        public String toString()
        {
            return String.format("Note: %s tickStart: %s tickEnd: %s", note, tickStart, tickEnd);
        }
    }

    public static void main(String[] args) throws IOException, InvalidMidiDataException, InterruptedException, ExecutionException
    {
        try (InputStream is = new FileInputStream(""))
        {
            final long startTime = System.currentTimeMillis();
            final ImmutablePair<String, BufferedImage> res = new MidiImageGeneration(is, 119, 5000).buildImage();
            final long endTime = System.currentTimeMillis();

            Logger.info(String.format("startTime: %s endTime: %s totalTime: %s file: %s", startTime, endTime, endTime-startTime, res.getLeft()));
            ImageIO.write(res.getRight(), "png", new File(String.format("/tmp/%s.png", res.getLeft())));
        }
    }
}
