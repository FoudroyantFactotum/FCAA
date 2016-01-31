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

import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_TESR;
import com.foudroyantfactotum.mod.fousarchive.midi.generation.LiveImage;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.BitSet;

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

    // TODO: 19/01/16 fix mirror render

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
            //Piano Roll Music
            final double displayAmount = 500 / 8048.0;
            final double shift = te.songPos;

            GlStateManager.bindTexture(
                    LiveImage.INSTANCE.getSong(
                            te.loadedSong.getSongResource()
                    ).getGlTextureId()
            );

            wr.setTranslation(x-0.02, y + 0.8, z - 0.8);
            wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            {
                wr.pos(1.33031+0.08, 0.88632, 1.25589).tex(0.5 * displayAmount + shift, 1).endVertex();
                wr.pos(0.7103-0.08, 0.88632, 1.25589).tex(0.5 * displayAmount + shift, 0).endVertex();
                wr.pos(0.7103-0.08, 0.66802, 1.350171).tex(shift, 0).endVertex();
                wr.pos(1.33031+0.08, 0.66802, 1.350171).tex(shift, 1).endVertex();

                wr.pos(1.33031+0.08, 0.66802, 1.350171).tex(shift, 1).endVertex();
                wr.pos(0.7103-0.08, 0.66802, 1.350171).tex(shift, 0).endVertex();
                wr.pos(0.7103-0.08, 0.46830, 1.25589).tex(-0.5*displayAmount +shift, 0).endVertex();
                wr.pos(1.33031+0.08, 0.46830, 1.25589).tex(-0.5*displayAmount + shift, 1).endVertex();
            }
            tess.draw();
        }
        wr.setTranslation(0.0D, 0.0D, 0.0D);

        RenderHelper.enableStandardItemLighting();
    }
}
