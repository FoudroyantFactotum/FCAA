package com.foudroyantfactotum.mod.fousarchive.blocks.Structure;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Block;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Instance;
import com.foudroyantfactotum.tool.structure.block.StructureShapeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@Auto_Block(name="fa_shape", tileEntity = FA_ShapeTE.class)
public class FA_ShapeBlock extends StructureShapeBlock
{
    @Auto_Instance
    public static final FA_ShapeBlock INSTANCE = null;

    public FA_ShapeBlock()
    {
        super(false);
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
