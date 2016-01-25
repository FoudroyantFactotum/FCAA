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
package com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano;

import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.BlockStructure;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.TEStructure;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.midi.midiPlayer.MidiPianoPlayer;
import com.foudroyantfactotum.mod.fousarchive.structure.StructureDefinitionBuilder;
import com.foudroyantfactotum.mod.fousarchive.structure.coordinates.BlockPosUtil;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Instance;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Structure;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraft.block.BlockDirectional.FACING;

@Auto_Structure(name = "playerPiano", tileEntity = TEPlayerPiano.class, TESR = TESRPlayerPiano.class)
public final class BlockPlayerPiano extends BlockStructure
{
    @Auto_Instance
    public static final BlockPlayerPiano INSTANCE = null;

    public static final PropertyEnum<PianoState> propPiano = PropertyEnum.create("ps", PianoState.class);

    public BlockPlayerPiano()
    {
        setDefaultState(
                this.blockState
                        .getBaseState()
                        .withProperty(FACING, EnumFacing.SOUTH)
                        .withProperty(MIRROR, false)
                        .withProperty(propPiano, PianoState.piano)
        );
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, MIRROR, propPiano);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(propPiano, PianoState.piano);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEPlayerPiano(getPattern(), state.getValue(FACING), state.getValue(MIRROR));
    }

    @Override
    public void spawnBreakParticle(World world, TEStructure te, BlockPos local, float sx, float sy, float sz)
    {

    }

    @Override
    public boolean onStructureBlockActivated(World world, BlockPos pos, EntityPlayer player, BlockPos callPos, EnumFacing side, BlockPos local, float sx, float sy, float sz)
    {
        final TileEntity ute = world.getTileEntity(pos);

        if (ute instanceof TEPlayerPiano)
        {
            final TEPlayerPiano te = (TEPlayerPiano) ute;
            final ItemStack uStackItem = player.inventory.getCurrentItem();

            if (te.loadedSong == null)
            {
                if (uStackItem != null && uStackItem.getItem() instanceof ItemPianoRoll)
                {
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.removeStackFromSlot(player.inventory.currentItem);

                    te.loadedSong = (ItemPianoRoll) uStackItem.getItem();
                }
            } else
            {
                if (te.hasSongTerminated)
                {
                    if (player.isSneaking())
                    {
                        if (!player.capabilities.isCreativeMode)
                            world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(te.loadedSong, 1)));

                        te.loadedSong = null;
                        te.songPos = 0.0;
                    } else {
                        te.isSongPlaying = true;
                        te.isSongRunning = true;
                        te.hasSongTerminated = false;

                        TEPlayerPiano.midiService.execute(new MidiPianoPlayer(te, te.songPos));
                    }
                } else if (te.isSongPlaying)
                {
                    te.isSongPlaying = false;
                }
            }
        }

        return super.onStructureBlockActivated(world, pos, player, callPos, side, local, sx, sy, sz);
    }

    @Override
    public StructureDefinitionBuilder getStructureBuild()
    {
        final StructureDefinitionBuilder builder = new StructureDefinitionBuilder();

        builder.assignConstructionDef(ImmutableMap.of(
                'j', "minecraft:jukebox",
                'p', "minecraft:planks"

        ));

        builder.assignConstructionBlocks(
                new String[]{
                        "pp"
                },
                new String[]{
                        "jj"
                }
        );

        builder.assignToolFormPosition(BlockPosUtil.of(0, 0, 0));

        builder.setConfiguration(BlockPosUtil.of(0, 0, 0),
                new String[]{
                        "M-"
                },
                new String[]{
                        "--"
                }
        );

        builder.setCollisionBoxes(new float[]{0, 0, 0, 0, 0, 0});

        return builder;
    }
}
