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
package com.foudroyantfactotum.mod.fousarchive.proxy;

import com.foudroyantfactotum.mod.fousarchive.TESR.FA_TESR;
import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Structure;
import com.foudroyantfactotum.tool.structure.net.StructureNetwork;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.io.IOException;
import java.io.InputStream;

public class ClientRenderProxy implements IModRenderProxy
{
    public void registerBlockAsItemModel(Block block) {
        final String resourceName = block.getUnlocalizedName().substring(5); //removes tile. off of the resource name

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(block),
                0,
                new ModelResourceLocation(resourceName, "inventory")
        );
    }

    public void registerMetaItemModel(Item item, int meta, ModelResourceLocation ml)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, ml);
    }

    @Override
    public <T extends ResourceLocation> void registerItemVariants(Item item, T... names)
    {
        ModelLoader.registerItemVariants(item, names);
    }

    @Override
    public void preInit()
    {
        OBJLoader.INSTANCE.addDomain(TheMod.MOD_ID);
        StructureNetwork.init(new SimpleNetworkWrapper(TheMod.MOD_ID));
    }

    @Override
    public void registerTESR(Auto_Structure annot) throws IllegalAccessException, InstantiationException
    {
        if (annot.TESR() != FA_TESR.class)
            ClientRegistry.bindTileEntitySpecialRenderer((Class) annot.tileEntity(), (FA_TESR) annot.TESR().newInstance());
    }

    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException
    {
        return Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
    }
}
