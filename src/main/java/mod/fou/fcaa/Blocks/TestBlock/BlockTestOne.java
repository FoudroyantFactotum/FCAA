package mod.fou.fcaa.Blocks.TestBlock;

import mod.fou.fcaa.Blocks.FCAA_Block;
import mod.fou.fcaa.utility.annotations.Auto_Block;
import mod.fou.fcaa.utility.annotations.Auto_Instance;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Auto_Block(name = "testBlockOne", tileEntity = TETestOne.class)
public class BlockTestOne extends FCAA_Block
{
    @Auto_Instance
    public static final BlockTestOne INSTANCE = null;

    public BlockTestOne()
    {
        super(Material.anvil);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TETestOne();
    }
}
