package com.foudroyantfactotum.mod.fousarchive.items;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FA_Item extends Item
{
    @Override
    public String getUnlocalizedName()
    {
        final String unloc = super.getUnlocalizedName();

        return TheMod.MOD_ID + ":" + unloc.substring(unloc.indexOf('.') + 1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }
}
