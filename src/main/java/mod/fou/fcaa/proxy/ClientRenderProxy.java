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
package mod.fou.fcaa.proxy;

import mod.fou.fcaa.Blocks.FCAA_TE;
import mod.fou.fcaa.Blocks.Structure.FCAA_TESR;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientRenderProxy extends RenderProxy
{
    public <E extends FCAA_TE> void registerTESR(Class<E> te, FCAA_TESR<E> tesr)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(te, tesr);
    }

    public void registerBlockAsItemModel(Block block) {
        final String resourceName = block.getUnlocalizedName().substring(5); //removes tile. off of the resource name

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(block),
                0,
                new ModelResourceLocation(resourceName, "inventory")
        );
    }
}
