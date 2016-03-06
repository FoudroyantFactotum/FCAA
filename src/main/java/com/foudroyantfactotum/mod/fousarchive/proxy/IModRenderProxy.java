package com.foudroyantfactotum.mod.fousarchive.proxy;

import com.foudroyantfactotum.mod.fousarchive.TESR.FA_TESR;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Structure;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public interface IModRenderProxy
{
    default void preInit() { }

    default void init() { }

    default void postInit() { }

    default <E extends TileEntity> void registerTESR(Class<E> te, FA_TESR<E> tesr) { }

    default void registerBlockAsItemModel(Block block) { }

    default void registerMetaItemModel(Item item, int meta, ModelResourceLocation ml) { }

    default void registerTESR(Auto_Structure annot) throws IllegalAccessException, InstantiationException { }

    default <T extends ResourceLocation> void registerItemVariants(Item item, T... names) { }

    InputStream getInputStream(ResourceLocation rl) throws IOException;
}
