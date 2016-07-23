/*
 * Fou's Archive - A Minecraft Mod
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foudroyantfactotum.mod.fousarchive.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public interface IModRenderProxy
{
    default void preInit() { }

    default void init() { }

    default void postInit() { }

    default void registerBlockAsItemModel(Block block) { }

    default void registerMetaItemModel(Item item, int meta, ModelResourceLocation ml) { }

    default <T extends ResourceLocation> void registerItemVariants(Item item, T... names) { }

    InputStream getInputStream(ResourceLocation rl) throws IOException;
}
