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
package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import com.foudroyantfactotum.mod.fousarchive.library.ModNames;
import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.proxy.ClientRenderProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class InitItem
{
    public static void init()
    {
        registerItems();
    }

    private static void registerItems()
    {
        GameRegistry.register(ModItems.pianoRoll);
        GameRegistry.register(ModItems.tuningFork);

        if (TheMod.proxy instanceof ClientRenderProxy)
        {
            registerSpecialPianoRollIcons();

            registerModelForItem(ModItems.tuningFork, ModNames.Items.tuningFork);
        }
    }

    private static void registerModelForItem(final Item item, final String modelLocation)
    {
        ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(new ResourceLocation(TheMod.MOD_ID, modelLocation), "")
        );
    }

    private static void registerSpecialPianoRollIcons()
    {
        TheMod.proxy.registerItemVariants(ModItems.pianoRoll,
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll0"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll1"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll2"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll3"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll4"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll5"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll6"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll7"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll8")
        );

        for (int i = 0; i < ItemPianoRoll.iconNo; ++i)
        {
            final ResourceLocation rl = new ResourceLocation(TheMod.MOD_ID, "pianoRoll"+i);

            TheMod.proxy.registerMetaItemModel(ModItems.pianoRoll, i,
                            new ModelResourceLocation(rl, "inventory")
                    );
        }
    }
}
