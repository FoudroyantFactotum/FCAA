package com.foudroyantfactotum.mod.fousarchive.blocks.Structure;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.tool.structure.block.StructureShapeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class FA_ShapeBlock extends StructureShapeBlock
{
    @Override
    public boolean canMirror()
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new FA_ShapeTE();
    }

    @Override
    public String getUnlocalizedName()
    {
        final String unloc = super.getUnlocalizedName();

        return "tile." + TheMod.MOD_ID + ":" + unloc.substring(unloc.indexOf('.') + 1);
    }
}
