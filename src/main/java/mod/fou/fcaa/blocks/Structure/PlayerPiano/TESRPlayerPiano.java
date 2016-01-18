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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import static mod.fou.fcaa.blocks.Structure.PlayerPiano.BlockPlayerPiano.propPiano;
import static mod.fou.fcaa.blocks.Structure.PlayerPiano.PianoState.key_white;

public class TESRPlayerPiano extends FCAA_TESR<TEPlayerPiano>
{
    @Override
    public void renderTileEntityAt(TEPlayerPiano te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        final BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        final BlockPos pos = te.getPos();
        final IBlockState state = getWorld().getBlockState(pos).withProperty(propPiano, key_white);
        final IBakedModel model = brd.getModelFromBlockState(state, getWorld(), pos);

        //bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled() ? 7425 : 7424);

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        worldRenderer.setTranslation(x - pos.getX(), y - pos.getY()+2, z - pos.getZ());
        worldRenderer.color(255, 255, 255, 255);

        brd.getBlockModelRenderer().renderModel(te.getWorld(), model, state, te.getPos(), worldRenderer);

        worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
    }
}
