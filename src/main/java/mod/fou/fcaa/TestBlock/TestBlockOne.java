package mod.fou.fcaa.TestBlock;

import mod.fou.fcaa.init.annotations.Auto_Block;
import mod.fou.fcaa.init.annotations.Auto_Instance;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Auto_Block(name = "testBlockOne")
public class TestBlockOne extends FCAA_Block
{
    @Auto_Instance
    public static final TestBlockOne INSTANCE = null;

    public TestBlockOne()
    {
        super(Material.anvil);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return true;
    }
}
