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
