package mod.fou.fcaa.Blocks.Structure.PlayerPiano;

import com.google.common.collect.ImmutableMap;
import mod.fou.fcaa.Blocks.Structure.BlockStructure;
import mod.fou.fcaa.Blocks.Structure.TEStructure;
import mod.fou.fcaa.structure.StructureDefinitionBuilder;
import mod.fou.fcaa.structure.coordinates.BlockPosUtil;
import mod.fou.fcaa.utility.annotations.Auto_Instance;
import mod.fou.fcaa.utility.annotations.Auto_Structure;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Auto_Structure(name = "playerPiano", tileEntity = TEPlayerPiano.class)
public class BlockPlayerPiano extends BlockStructure
{
    @Auto_Instance
    public static final BlockPlayerPiano INSTANCE = null;

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEPlayerPiano(getPattern(), state.getValue(BlockDirectional.FACING), state.getValue(MIRROR));
    }

    @Override
    public void spawnBreakParticle(World world, TEStructure te, BlockPos local, float sx, float sy, float sz)
    {

    }

    @Override
    public StructureDefinitionBuilder getStructureBuild()
    {
        final StructureDefinitionBuilder builder = new StructureDefinitionBuilder();

        builder.assignConstructionDef(ImmutableMap.of(
                'p', "minecraft:dirt"
        ));

        builder.assignConstructionBlocks(
                new String[]{
                        "  "
                },
                new String[]{
                        "  "
                }
        );

        builder.assignToolFormPosition(BlockPosUtil.of(0,0,0));

        builder.setConfiguration(BlockPosUtil.of(0,0,0),
                new String[]{
                        "M-"
                },
                new String[]{
                        "--"
                }
        );

        builder.setCollisionBoxes(new float[]{0,0,0 ,0,0,0});

        return builder;
    }
}
