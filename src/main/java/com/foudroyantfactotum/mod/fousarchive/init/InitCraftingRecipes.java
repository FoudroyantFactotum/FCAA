package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class InitCraftingRecipes
{
    private InitCraftingRecipes()
    {
        //noop
    }

    public static void init()
    {
        GameRegistry.addRecipe(new ItemStack(ModItems.tuningFork),
                " I ",
                " II",
                "S  ",
                'I', new ItemStack(Items.IRON_INGOT),
                'S', new ItemStack(Items.STICK)
        );
    }
}
