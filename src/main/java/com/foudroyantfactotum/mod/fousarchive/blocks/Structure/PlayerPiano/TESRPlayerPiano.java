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

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_TESR;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;

import static com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.BlockPlayerPiano.propPiano;
import static com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.PianoState.*;

public class TESRPlayerPiano extends FA_TESR<TEPlayerPiano>
{
    private static final BitSet blackKeyNo = new BitSet(85);
    private static final float keySize = 0.0375f;

    public TESRPlayerPiano()
    {
        int[] bk = {1, 4, 6, 9, 11, 13, 16, 18, 21, 23, 25, 28, 30, 33, 35, 37, 40, 42, 45, 47, 49, 52, 54, 57, 59, 61, 64, 66, 69, 71, 73, 76, 78, 81, 83};

        for (int v : bk)
            blackKeyNo.set(v, true);
    }

    @Override
    public void renderTileEntityAt(TEPlayerPiano te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        final BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        final Tessellator tess = Tessellator.getInstance();
        final WorldRenderer wr = tess.getWorldRenderer();

        final BlockPos pos = te.getPos();
        final IBlockState statePiano = getWorld().getBlockState(pos).withProperty(propPiano, piano_body);
        final IBlockState stateKeyWhite = statePiano.withProperty(propPiano, key_white);
        final IBlockState stateKeyBlack = statePiano.withProperty(propPiano, key_black);

        final IBakedModel modelPianoBody = brd.getBlockModelShapes().getModelForState(statePiano);
        final IBakedModel modelKeyWhite = brd.getBlockModelShapes().getModelForState(stateKeyWhite);
        final IBakedModel modelKeyBlack = brd.getBlockModelShapes().getModelForState(stateKeyBlack);

        final EnumFacing orientation = statePiano.getValue(BlockDirectional.FACING);

        bindTexture(TextureMap.locationBlocksTexture);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);

        double rx = x - pos.getX();
        double ry = y - pos.getY();
        double rz = z - pos.getZ();

        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        wr.color(255, 255, 255, 255);
        wr.setTranslation(rx,ry,rz);

        brd.getBlockModelRenderer().renderModel(te.getWorld(), modelPianoBody, statePiano, te.getPos(), wr);

        //render keys
        for (int key = 0; key < 85; ++key)
        {
            rx -= keySize * orientation.getFrontOffsetZ();
            rz += keySize * orientation.getFrontOffsetX();

            wr.setTranslation(rx, ry + te.keyOffset[key], rz);

            if (blackKeyNo.get(key))
            {
                brd.getBlockModelRenderer().renderModel(te.getWorld(), modelKeyBlack, stateKeyWhite, te.getPos(), wr);
                rx += keySize * orientation.getFrontOffsetZ();
                rz -= keySize * orientation.getFrontOffsetX();
            } else
            {
                brd.getBlockModelRenderer().renderModel(te.getWorld(), modelKeyWhite, stateKeyWhite, te.getPos(), wr);
            }
        }

        tess.draw();

        if (te.loadedSong != null)
        {
            final MidiTexture tex = LiveImage.INSTANCE.getSong(te.loadedSong);

            if (tex != null)
            {
                //Piano Roll Music
                GlStateManager.bindTexture(tex.getGlTextureId());

                wr.setTranslation(x - 0.02, y + 0.8, z - 0.8);
                wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                {
                    addRender(wr, te.songPos, objQUAD[orientation.getHorizontalIndex()]);
                }
                tess.draw();
            }
        }
        wr.setTranslation(0.0D, 0.0D, 0.0D);

        RenderHelper.enableStandardItemLighting();
        loadSheetModel();
    }

    private static class Quad
    {
        public final float[] a;
        public final float[] b;
        public final float[] c;
        public final float[] d;

        public Quad(float[] a, float[] b, float[] c, float[] d)
        {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        @Override
        public String toString()
        {
            return "Quad{" +
                    "a=" + Arrays.toString(a) +
                    ", b=" + Arrays.toString(b) +
                    ", c=" + Arrays.toString(c) +
                    ", d=" + Arrays.toString(d) +
                    '}';
        }
    }

    private static final ResourceLocation sheetLocation = new ResourceLocation(TheMod.MOD_ID, "models/block/PlayerPiano/PlayerPiano-Sheet.ply");
    private static Quad[][] objQUAD;

    static {
        loadSheetModel();
    }

    private static void loadSheetModel()
    {
        final Pair<float[][], int[][]> pointsAndFaces = getQuadPointData();
        final float[][] offset = {
                {1.01f, -0.8f, 0.78f}, //south
                {1.05f, -0.8f, -0.2f}, //west
                {0.02f, -0.8f, 1.82f}, //north
                {0.0f, -0.8f, 2.8f}  //east
        };

        objQUAD = new Quad[4][pointsAndFaces.second().length];

        for (int i = 0; i < 4; ++i)
        {
            final float[][] points = transAndRot(pointsAndFaces.first(), offset[i], EnumFacing.getHorizontal(i));

            for (int ii = 0; ii < objQUAD[i].length; ++ii)
            {
                final int[] faces = pointsAndFaces.second()[ii];

                objQUAD[i][ii] =  new Quad(points[faces[0]], points[faces[1]], points[faces[2]], points[faces[3]]);
            }
        }
    }

    private static float[][] transAndRot(float[][] oldPoints, float[] offset, EnumFacing f)
    {
        final float[][] points = new float[oldPoints.length][];
        final Vec3i d = f.getDirectionVec();

        for (int i = 0; i < oldPoints.length; ++i)
        {
            points[i] = Arrays.copyOf(oldPoints[i], 5);

            //t & r x,y,z
            points[i][0] = d.getZ() * oldPoints[i][0] + d.getX() * oldPoints[i][2] + offset[0];
            points[i][1] += offset[1];
            points[i][2] = d.getZ() * oldPoints[i][2] + d.getX() * oldPoints[i][0] + offset[2];

            //f uv
            points[i][3] = f.getAxis() == EnumFacing.Axis.X ? -points[i][3] : points[i][3];
        }

        return points;
    }

    private static Pair<float[][], int[][]> getQuadPointData()
    {
        try (final InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(sheetLocation).getInputStream())
        {
            final Scanner scanner = new Scanner(stream).useDelimiter("\n");

            int elementFace = -1;
            int elementVertex = -1;
            boolean readingHeader = true;

            while (scanner.hasNext())
            {
                if (readingHeader)
                {
                    final String line = scanner.next();

                    if (line.startsWith("element"))
                    {
                        final String[] words = line.split(" ");

                        if (words[1].equals("vertex"))
                        {
                            elementVertex = Integer.parseInt(words[2]);
                        } else if (words[1].equals("face"))
                        {
                            elementFace = Integer.parseInt(words[2]);
                        }
                    } else if (line.startsWith("end_header"))
                    {
                        if (elementFace == -1)
                            throw new FousArchiveException("missing 'element face' in ply");
                        if (elementVertex == -1)
                            throw new FousArchiveException("missing 'element vertex' in ply");

                        readingHeader = false;
                    }
                } else
                {
                    final int[][] faceIndices = new int[elementFace][];
                    final float[][] points = new float[elementVertex][];

                    //read all Verities
                    for (int i = 0; i < elementVertex; ++i)
                    {
                        final String[] line = scanner.next().split(" ");
                        final float[] vertex = new float[5];

                        for (int ii = 0; ii < vertex.length; ++ii)
                            vertex[ii] = Float.parseFloat(line[ii]);

                        points[i] = vertex;
                    }

                    //read all face indices
                    for (int i = 0; i < elementFace; ++i)
                    {
                        final String[] line = scanner.next().split(" ");
                        final int n = Integer.parseInt(line[0]);

                        if (n != 4)
                            throw new FousArchiveException("ply contains face with more then 4 verities");

                        final int a = Integer.parseInt(line[1]);
                        final int b = Integer.parseInt(line[2]);
                        final int c = Integer.parseInt(line[3]);
                        final int d = Integer.parseInt(line[4]);

                        faceIndices[i] = new int[]{a, b, c, d};
                    }

                    return Pair.of(points, faceIndices);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        throw new FousArchiveException("Unexpected End Of Function");
    }

    private final static double d = 500 / 8048.0;
    private final static double hd = d * 0.5;

    private static void addRender(WorldRenderer wr, double shift, Quad[] quads)
    {
        for (final Quad q : quads)
        {
            float[] v;

            v = q.a; wr.pos(v[0], v[1], v[2]).tex(v[4] * d + shift - hd, v[3]).endVertex();
            v = q.b; wr.pos(v[0], v[1], v[2]).tex(v[4] * d + shift - hd, v[3]).endVertex();
            v = q.c; wr.pos(v[0], v[1], v[2]).tex(v[4] * d + shift - hd, v[3]).endVertex();
            v = q.d; wr.pos(v[0], v[1], v[2]).tex(v[4] * d + shift - hd, v[3]).endVertex();
        }
    }
}
