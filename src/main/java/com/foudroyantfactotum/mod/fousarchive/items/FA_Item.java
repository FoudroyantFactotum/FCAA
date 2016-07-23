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
