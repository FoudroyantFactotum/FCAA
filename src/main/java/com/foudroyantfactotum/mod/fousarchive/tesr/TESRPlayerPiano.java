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
package com.foudroyantfactotum.mod.fousarchive.tesr;

import com.foudroyantfactotum.mod.fousarchive.library.ModBlocks;
import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.TEPlayerPiano;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.MidiTexture;
import com.foudroyantfactotum.mod.fousarchive.utility.ply.ModelLoader;
import com.foudroyantfactotum.mod.fousarchive.utility.ply.Quad;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.BitSet;

import static com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.BlockPlayerPiano.propPiano;
import static com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.PianoState.*;

public class TESRPlayerPiano extends FA_TESR<TEPlayerPiano>
{
    private static final BitSet blackKeyNo = new BitSet(85);
    private static final float keySize = 0.0375f;

    private static Quad[][] objQUAD;

    static {
        ModelLoader.registerLoad(
                new ResourceLocation(TheMod.MOD_ID, "models/block/PlayerPiano/PlayerPiano-Sheet.ply"),
                TESRPlayerPiano::getSheetTransform,
                TESRPlayerPiano::setPianoRollSheetQuads
        );
    }

    public TESRPlayerPiano()
    {
        int[] bk = {1, 4, 6, 9, 11, 13, 16, 18, 21, 23, 25, 28, 30, 33, 35, 37, 40, 42, 45, 47, 49, 52, 54, 57, 59, 61, 64, 66, 69, 71, 73, 76, 78, 81, 83};

        for (int v : bk)
            blackKeyNo.set(v, true);
    }

    private static void setPianoRollSheetQuads(Quad[][] q)
    {
        objQUAD = q;
    }

    private static float[][] getSheetTransform()
    {
        return new float[][] {
                {1.01f, -0.8f, 0.795f}, //south
                {1.025f, -0.8f, -0.2f}, //west
                {0.02f, -0.8f, 1.805f}, //north
                {0.015f, -0.8f, 2.8f}  //east
        };
    }

    @Override
    public void renderTileEntityAt(TEPlayerPiano te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        final BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        final Tessellator tess = Tessellator.getInstance();
        final VertexBuffer wr = tess.getBuffer();

        final BlockPos pos = te.getPos();
        final IBlockState state = getWorld().getBlockState(pos);

        if (state.getBlock() != ModBlocks.playerPiano) return;

        final IBlockState statePiano = state.withProperty(propPiano, piano_body);
        final IBlockState stateKeyWhite = statePiano.withProperty(propPiano, key_white);
        final IBlockState stateKeyBlack = statePiano.withProperty(propPiano, key_black);

        final IBakedModel modelPianoBody = brd.getBlockModelShapes().getModelForState(statePiano);
        final IBakedModel modelKeyWhite = brd.getBlockModelShapes().getModelForState(stateKeyWhite);
        final IBakedModel modelKeyBlack = brd.getBlockModelShapes().getModelForState(stateKeyBlack);

        final EnumFacing orientation = statePiano.getValue(BlockHorizontal.FACING);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

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

        brd.getBlockModelRenderer().renderModel(te.getWorld(), modelPianoBody, statePiano, te.getPos(), wr, false);

        //render keys
        for (int key = 0; key < 85; ++key)
        {
            rx -= keySize * orientation.getFrontOffsetZ();
            rz += keySize * orientation.getFrontOffsetX();

            wr.setTranslation(rx, ry + te.keyOffset[key], rz);

            if (blackKeyNo.get(key))
            {
                brd.getBlockModelRenderer().renderModel(te.getWorld(), modelKeyBlack, stateKeyWhite, te.getPos(), wr, false);
                rx += keySize * orientation.getFrontOffsetZ();
                rz -= keySize * orientation.getFrontOffsetX();
            } else
            {
                brd.getBlockModelRenderer().renderModel(te.getWorld(), modelKeyWhite, stateKeyWhite, te.getPos(), wr, false);
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
                    addRender(wr, tex, te.songPos, objQUAD[orientation.getHorizontalIndex()]);
                }
                tess.draw();
            }
        }
        wr.setTranslation(0.0D, 0.0D, 0.0D);

        RenderHelper.enableStandardItemLighting();
    }

    private static void addRender(VertexBuffer wr, MidiTexture mdt, double shift, Quad[] quads)
    {
        double d = mdt.getDisplayRatio();
        double hd = mdt.getDisplayAmount();

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
