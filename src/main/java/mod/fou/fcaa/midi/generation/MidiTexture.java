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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MidiTexture extends AbstractTexture
{
    final ResourceLocation rl;

    public MidiTexture(ResourceLocation rl)
    {
        this.rl = rl;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        final int xSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        final int ySize = 88;
        ImmutablePair<String, byte[]> res = null;

        try (final InputStream io = resourceManager.getResource(rl).getInputStream())
        {
            res = getMidiTrack(io, xSize, ySize);
        } catch (InvalidMidiDataException e)
        {
            e.printStackTrace();
        }

        if (res != null)
        {
            this.deleteGlTexture();

            GlStateManager.bindTexture(this.getGlTextureId());
            
            final ByteBuffer bb = (ByteBuffer) BufferUtils
                    .createByteBuffer(res.getRight().length)
                    .put(res.getRight())
                    .flip();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, xSize, ySize, 0,
                    GL11.GL_RGB, GL11.GL_BYTE, bb
            );
        }
    }

    @Override
    public void restoreLastBlurMipmap()
    {

    }

    @Override
    public void setBlurMipmap(boolean p1, boolean p2)
    {

    }

    private static ImmutablePair<String, byte[]> getMidiTrack(InputStream io, int xSize, int ySize) throws InvalidMidiDataException, IOException
    {
        final Sequence sequence = MidiSystem.getSequence(io);
        final Track[] midiTrack = sequence.getTracks();
        final byte pixelComponents = 3;

        String songName = "noName";
        final byte[] img = new byte[xSize * ySize * pixelComponents];
        final long[] lastEvent = new long[88];

        Arrays.fill(lastEvent, -1);

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

                if (note < ySize && note > -1)
                {
                    if (vel == 0 && lastEvent[note] != -1)
                    {
                        final int line = note * xSize;
                        final int lineStart = (int) (line + (lastEvent[note]*xSize / (double)sequence.getTickLength())) * pixelComponents;
                        final int lineEnd = (int) (line + (event.getTick()*xSize / (double)sequence.getTickLength())) * pixelComponents;

                        Arrays.fill(img, lineStart, lineEnd, Byte.MAX_VALUE);

                        lastEvent[note] = -1;

                    } else if (lastEvent[note] == -1)
                    {
                        lastEvent[note] = event.getTick();
                    }
                }
            }
        }

        return ImmutablePair.of(songName, img);
    }

    public static void main(String[] args) throws IOException, InvalidMidiDataException
    {
        final InputStream io = new FileInputStream("");
        final long startTime = System.currentTimeMillis();

        final ImmutablePair<String, byte[]> test = getMidiTrack(io, 8192, 88);

        final long endTime = System.currentTimeMillis();

        io.close();

        Logger.info(String.format("startTime: %s endTime: %s totalTime: %s file: %s", startTime, endTime, endTime - startTime, test.getLeft()));


        ImageIO.write(toBufferImage(test.getRight(), 8192, 88), "png", new File(String.format("/tmp/%s.png", test.getLeft())));
    }

    private static BufferedImage toBufferImage(byte[] img, int xSize, int ySize)
    {
        final BufferedImage bimg = new BufferedImage(xSize, ySize, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < xSize; ++x)
        {
            for (int y = 0; y < ySize; ++y)
            {
                final int pixel = (y*xSize + x)*3;

                bimg.setRGB(x, y, img[pixel] << 16 | img[pixel+1] << 8 | img[pixel+2]);
            }
        }

        return bimg;
    }
}
