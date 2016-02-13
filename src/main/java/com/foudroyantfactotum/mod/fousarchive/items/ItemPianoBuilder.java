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
package com.foudroyantfactotum.mod.fousarchive.items;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.BlockPlayerPiano;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Instance;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Item;
import com.foudroyantfactotum.tool.structure.block.StructureBlock;
import com.foudroyantfactotum.tool.structure.item.BuildFormTool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Auto_Item(name = "pianoFormTool")
public class ItemPianoBuilder extends BuildFormTool
{
    @Auto_Instance
    public static final ItemPianoBuilder INSTANCE = null;

    private static final List<StructureBlock> validSearchBlocks = new ArrayList<>(1);

    public ItemPianoBuilder()
    {
        validSearchBlocks.add(BlockPlayerPiano.INSTANCE);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote || player == null)
        {
            return true;
        }

        final EnumFacing[] orientation = orientationPriority[MathHelper.floor_double(player.rotationYaw * 4.0f / 360.0f + 0.5) & 3];
        final boolean[] mirror = mirrorPriority[player.isSneaking()?1:0];

        doSearch(world, pos, orientation, mirror, validSearchBlocks);

        return true;
    }

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
