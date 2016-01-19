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
package mod.fou.fcaa.blocks.Structure.PlayerPiano;

import mod.fou.fcaa.blocks.Structure.FCAA_TESR;
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
import org.lwjgl.opengl.GL11;

import java.util.BitSet;

import static mod.fou.fcaa.blocks.Structure.PlayerPiano.BlockPlayerPiano.propPiano;
import static mod.fou.fcaa.blocks.Structure.PlayerPiano.PianoState.key_black;
import static mod.fou.fcaa.blocks.Structure.PlayerPiano.PianoState.key_white;

public class TESRPlayerPiano extends FCAA_TESR<TEPlayerPiano>
{
    public final ResourceLocation rl = new ResourceLocation("fcaa", "midi/Hindustan(1918).img");

    private static final BitSet blackKeyNo = new BitSet(88);
    private static final float keySize = 0.036f;

    public TESRPlayerPiano()
    {
        int[] bk = {1, 4, 6, 9, 11, 13, 16, 18, 21, 23, 25, 28, 30, 33, 35, 37, 40, 42, 45, 47, 49, 52, 54, 57, 59, 61, 64, 66, 69, 71, 73, 76, 78, 81, 83, 85};

        for (int v : bk)
        {
            blackKeyNo.set(v, true);
        }
    }

    // TODO: 19/01/16 fix mirror render

    @Override
    public void renderTileEntityAt(TEPlayerPiano te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        final BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        final Tessellator tess = Tessellator.getInstance();
        final WorldRenderer wr = tess.getWorldRenderer();

        final BlockPos pos = te.getPos();
        final IBlockState statePiano = getWorld().getBlockState(pos);
        final IBlockState stateKeyWhite = statePiano.withProperty(propPiano, key_white);
        final IBlockState stateKeyBlack = statePiano.withProperty(propPiano, key_black);
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

        //render keys
        for (int key = 0; key < 88; ++key)
        {
            rx -= keySize * orientation.getFrontOffsetZ();
            rz += keySize * orientation.getFrontOffsetX();

            wr.setTranslation(rx, ry + te.keyOffset[key], rz);

            if (blackKeyNo.get(key))
            {
                brd.getBlockModelRenderer().renderModel(te.getWorld(), modelKeyBlack, stateKeyWhite, te.getPos(), wr);
                rx += keySize * orientation.getFrontOffsetZ();
                rz -= keySize * orientation.getFrontOffsetX();

            } else {
                brd.getBlockModelRenderer().renderModel(te.getWorld(), modelKeyWhite, stateKeyWhite, te.getPos(), wr);
            }
        }

        tess.draw();

        //Piano Roll Music
        final double displayAmount = 500/8048.0;
        final double shift = te.songReadHeadPos;

        te.songReadHeadPos += 0.0002;

        if (te.songReadHeadPos > 1)
            te.songReadHeadPos = 0;


        wr.setTranslation(x, y+0.8, z-0.8);
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        {
            wr.pos(0.7103, 0.66802, 1.350171).tex(0.0 * displayAmount + shift, 0).endVertex();
            wr.pos(1.33031, 0.66802, 1.350171).tex(0.0 * displayAmount + shift, 1).endVertex();
            wr.pos(1.33031, 0.46830 + 0.41802, 1.25589).tex(-0.5 * displayAmount + shift, 1).endVertex();
            wr.pos(0.7103, 0.46830 + 0.41802, 1.25589).tex(-0.5 * displayAmount + shift, 0).endVertex();

            wr.pos(1.33031, 0.66802, 1.350171).tex(shift, 1).endVertex();
            wr.pos(0.7103, 0.66802, 1.350171).tex(shift, 0).endVertex();
            wr.pos(0.67103, 0.46830, 1.25589).tex(0.5*displayAmount + shift, 0).endVertex();
            wr.pos(1.33031, 0.46830, 1.25589).tex(0.5*displayAmount + shift, 1).endVertex();
        }

        bindTexture(rl);

        tess.draw();
        wr.setTranslation(0.0D, 0.0D, 0.0D);
        //bindTexture(TextureMap.locationBlocksTexture);

        RenderHelper.enableStandardItemLighting();
    }
}
