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
package com.foudroyantfactotum.mod.fousarchive.utility.ply;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModelLoader
{
    private static ProgressManager.ProgressBar loadingBar = null;
    private static final List<ModelReference> modelList = new ArrayList<>();

    static {
        MinecraftForge.EVENT_BUS.register(new ModelLoader());
    }

    private ModelLoader() {/*noop*/}

    public static void registerLoad(@Nonnull ResourceLocation rl, @Nullable Supplier<float[][]> s, @Nonnull Consumer<Quad[][]> q)
    {
        modelList.add(new ModelReference(rl, s, q));
    }

    @SubscribeEvent
    public void onResourceManagerReload(ModelBakeEvent e)
    {
        loadingBar = ProgressManager.push("PLY Model Loader", modelList.size());

        for (final ModelReference mr : modelList)
        {
            loadingBar.step(mr.resource.toString());

            final PLYData pointsAndFaces = getQuadPointData(mr.resource);
            final Quad[][] objQUAD = new Quad[4][pointsAndFaces.faceIndices.length];
            final float[][] offset = mr.suppTransform.get();

            //for each Orientation
            for (int i = 0; i < 4; ++i)
            {
                final float[][] points = transAndRot(pointsAndFaces.vertices, offset[i], EnumFacing.getHorizontal(i));

                for (int ii = 0; ii < objQUAD[i].length; ++ii)
                {
                    final int[] faces = pointsAndFaces.faceIndices[ii];

                    objQUAD[i][ii] = new Quad(points[faces[0]], points[faces[1]], points[faces[2]], points[faces[3]]);
                }
            }

            mr.consQuads.accept(objQUAD);
        }

        ProgressManager.pop(loadingBar);
        loadingBar = null;
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

    private static PLYData getQuadPointData(ResourceLocation rl)
    {
        try (final InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream())
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

                        if (words[1].equals("vertex"))    elementVertex = Integer.parseInt(words[2]);
                        else if (words[1].equals("face")) elementFace   = Integer.parseInt(words[2]);
                    } else if (line.startsWith("end_header"))
                    {
                        if (elementFace == -1)
                            throw new FousPLYModelLoaderException("missing 'element face' in ply");
                        if (elementVertex == -1)
                            throw new FousPLYModelLoaderException("missing 'element vertex' in ply");

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
                            throw new FousPLYModelLoaderException("ply contains face with more then 4 verities");

                        final int a = Integer.parseInt(line[1]);
                        final int b = Integer.parseInt(line[2]);
                        final int c = Integer.parseInt(line[3]);
                        final int d = Integer.parseInt(line[4]);

                        faceIndices[i] = new int[]{a, b, c, d};
                    }

                    return new PLYData(points, faceIndices);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        throw new FousPLYModelLoaderException("Unexpected End Of Function");
    }

    private static class ModelReference
    {
        private static final Supplier<float[][]> emptySupp = () -> new float[4][3];

        public final ResourceLocation resource;
        public final Supplier<float[][]> suppTransform;
        public final Consumer<Quad[][]> consQuads;

        public ModelReference(ResourceLocation resource, Supplier<float[][]> suppTransform, Consumer<Quad[][]> consQuads)
        {
            this.resource = resource;
            this.suppTransform = suppTransform == null ? emptySupp : suppTransform;
            this.consQuads = consQuads;
        }
    }

    public static class PLYData
    {
        public final float[][] vertices;
        public final int[][] faceIndices;

        public PLYData(float[][] vertices, int[][] faceIndices)
        {
            this.vertices = vertices;
            this.faceIndices = faceIndices;
        }
    }
}
