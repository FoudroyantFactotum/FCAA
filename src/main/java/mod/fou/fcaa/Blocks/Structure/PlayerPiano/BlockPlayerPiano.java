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
