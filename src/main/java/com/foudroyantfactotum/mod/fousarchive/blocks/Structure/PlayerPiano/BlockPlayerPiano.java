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

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_StructureBlock;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import com.foudroyantfactotum.mod.fousarchive.midi.LiveMidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.MidiDetails;
import com.foudroyantfactotum.mod.fousarchive.midi.midiPlayer.MidiPianoPlayer;
import com.foudroyantfactotum.mod.fousarchive.midi.state.SongPlayingState;
import com.foudroyantfactotum.mod.fousarchive.utility.log.Logger;
import com.foudroyantfactotum.mod.fousarchive.utility.log.UserLogger;
import com.foudroyantfactotum.tool.structure.coordinates.BlockPosUtil;
import com.foudroyantfactotum.tool.structure.tileentity.StructureTE;
import com.foudroyantfactotum.tool.structure.utility.StructureDefinitionBuilder;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public final class BlockPlayerPiano extends FA_StructureBlock
{
    public static final PropertyEnum<PianoState> propPiano = PropertyEnum.create("ps", PianoState.class);

    public BlockPlayerPiano()
    {
        super(false);

        setDefaultState(
                this.blockState
                        .getBaseState()
                        .withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH)
                        .withProperty(propPiano, PianoState.piano)
        );
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, propPiano);
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
        return new TEPlayerPiano(getPattern(), state.getValue(BlockHorizontal.FACING));
    }

    @Override
    public void spawnBreakParticle(World world, StructureTE te, BlockPos local, float sx, float sy, float sz)
    {
        world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                    local.getX() + 0.5f,
                    local.getY() + 0.5f,
                    local.getZ() + 0.5f, (-0.5 + Math.random()) * 0.25f, 0.05f, (-0.5 + Math.random()) * 0.2f);
    }

    @Override
    public boolean onStructureBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumHand hand, BlockPos callPos, EnumFacing side, BlockPos local, float sx, float sy, float sz)
    {
        if (hand != EnumHand.MAIN_HAND) return false;

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

                    final NBTTagCompound nbt = uStackItem.getTagCompound();

                    if (nbt != null && nbt.hasKey(ItemPianoRoll.ROLL))
                    {
                        final ResourceLocation rl = new ResourceLocation(nbt.getString(ItemPianoRoll.ROLL));

                        try
                        {
                            TheMod.proxy.getInputStream(rl);
                            te.loadedSong = rl;
                        } catch (IOException e)
                        {
                            te.loadedSong = ItemPianoRoll.NONE;
                        }
                    } else
                    {
                        te.loadedSong = ItemPianoRoll.NONE;
                    }
                    Logger.info(UserLogger.MIDI_PIANO, "has set playerPiano roll " + te.loadedSong);
                } else {
                    Logger.info(UserLogger.MIDI_PIANO, "no playerPiano roll in hand");
                }
            } else
            {
                if (te.songState == SongPlayingState.TERMINATED)
                {
                    if (player.isSneaking())
                    {
                        if (!player.capabilities.isCreativeMode && !world.isRemote)
                        {
                            spawnItemPianoRollOnGround(world, pos, te.loadedSong);
                        }

                        te.loadedSong = null;
                        te.rollDisplayPosition = 0.0;
                        te.songPosition = 0;
                        Logger.info(UserLogger.MIDI_PIANO, "removed playerPiano roll");
                    } else
                    {
                        te.songState = SongPlayingState.PLAYING;
                        Logger.info(UserLogger.MIDI_PIANO, "------------started playing @" + te.songPosition + "------------");

                        final MidiPianoPlayer mpp = new MidiPianoPlayer(te, te.songPosition, FMLCommonHandler.instance().getEffectiveSide());
                        (world.isRemote ? TEPlayerPiano.existingPlayers_client : TEPlayerPiano.existingPlayers_server).put(pos, mpp);
                        TEPlayerPiano.midiService.execute(mpp);
                    }
                } else if (te.songState == SongPlayingState.PLAYING)
                {
                    te.songState = SongPlayingState.RUNNING;

                    Logger.info(UserLogger.MIDI_PIANO, "============is Stopping============");
                }
            }

            te.markDirty();
            world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), world.getBlockState(pos), world.getBlockState(pos), 0x2);
            world.notifyNeighborsOfStateChange(pos, this);
        }

        return super.onStructureBlockActivated(world, pos, player, hand, callPos, side, local, sx, sy, sz);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        final TileEntity ute = worldIn.getTileEntity(pos);

        if (ute instanceof TEPlayerPiano)
        {
            final TEPlayerPiano te = (TEPlayerPiano) ute;

            if (te.loadedSong != null)
            {
                final MidiDetails md = LiveMidiDetails.INSTANCE.getDetailsOnSong(te.loadedSong);

                if (md != MidiDetails.NO_DETAILS)
                {
                    Logger.info(UserLogger.GENERAL, "" + (int) ((double) te.songPosition/md.getMaxTicks() * 15));
                    return (int) ((double) te.songPosition/md.getMaxTicks() * 15);
                }
            }

            return 0;
        }

        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    @Override
    public void breakStructure(World world, BlockPos origin, EnumFacing orientation, boolean mirror, boolean isCreative, boolean isSneaking)
    {
        if (!world.isRemote)
        {
            final TileEntity ute = world.getTileEntity(origin);

            if (ute instanceof TEPlayerPiano)
            {
                final TEPlayerPiano te = (TEPlayerPiano) ute;
                spawnItemPianoRollOnGround(world, origin, te.loadedSong);
            }
        }

        super.breakStructure(world, origin, orientation, mirror, isCreative, isSneaking);
    }

    private static void spawnItemPianoRollOnGround(@Nonnull World world, @Nonnull BlockPos pos, @Nullable ResourceLocation rl)
    {
        if (rl != null && rl != ItemPianoRoll.NONE)
        {
            final String name = rl.toString();
            final ItemStack stack = new ItemStack(ModItems.pianoRoll, 1, Math.abs(name.hashCode()) % ItemPianoRoll.iconNo);
            final NBTTagCompound nbt = new NBTTagCompound();

            ItemPianoRoll.setPianoRollNBT(nbt, name);
            stack.setTagCompound(nbt);

            world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
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

        builder.setCollisionBoxes(
                new float[]{0.0f,0.0f,0.0f, 2.0f,2.0f, 0.46f},
                new float[]{0.0f,0.8f,0.45f, 2.0f,0.92f, 0.90f}
        );

        return builder;
    }
}
