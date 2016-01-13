/*
 * Copyright (c) 2014 Rosie Alexander and Scott Killen.
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
package mod.fou.fcaa.Blocks.Structure;

import mod.fou.fcaa.Blocks.FCAA_Block;
import mod.fou.fcaa.init.InitBlock;
import mod.fou.fcaa.structure.IStructure.ITEStructure;
import mod.fou.fcaa.structure.registry.StructureRegistry;
import mod.fou.fcaa.utility.annotations.Auto_Block;
import mod.fou.fcaa.utility.annotations.Auto_Instance;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import static mod.fou.fcaa.Blocks.Structure.BlockStructure.*;
import static mod.fou.fcaa.structure.coordinates.TransformLAG.localToGlobalCollisionBoxes;
import static net.minecraft.block.BlockDirectional.FACING;

@Auto_Block(name = "structureShape", tab = InitBlock.ModTab.none, tileEntity = TEStructureShape.class)
public class BlockStructureShape extends FCAA_Block implements ITileEntityProvider
{
    @Auto_Instance
    public static final BlockStructureShape INSTANCE = null;
    public static boolean _DEBUG = false;
    public static final AxisAlignedBB EMPTY_BOUNDS = AxisAlignedBB.fromBounds(0, 0, 0, 0, 0, 0);

    public BlockStructureShape()
    {
        super(Material.anvil);
        setDefaultState(this.blockState
                .getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(MIRROR, false)
        );
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING, MIRROR);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        final EnumFacing facing = EnumFacing.getHorizontal(meta & 0x3);
        final boolean mirror = (meta & 0x4) != 0;

        return getDefaultState()
                .withProperty(FACING, facing)
                .withProperty(MIRROR, mirror);
    }

    public int getMetaFromState(IBlockState state)
    {
        final EnumFacing facing = getOrientation(state);
        final boolean mirror = getMirror(state);

        return facing.getHorizontalIndex() | (mirror? 1<<2:0);
    }

    @Override
    public int quantityDropped(Random rnd)
    {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
    {
        final TEStructureShape te = (TEStructureShape) world.getTileEntity(pos);

        if (te != null && te.getMasterBlockInstance() != null)
        {
            return te.getMasterBlockInstance().getPickBlock(target, world, pos, player);
        }

        return null;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos)
    {
        final TEStructureShape te = (TEStructureShape) world.getTileEntity(pos);
        if (te != null)
        {
            final TileEntity te2 = world.getTileEntity(te.getMasterBlockLocation());

            if (te2 != null)
            {
                return te2.getRenderBoundingBox();
            }
        }

        return EMPTY_BOUNDS;
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
    {
        final ITEStructure te = (ITEStructure) world.getTileEntity(pos);

        if (te != null)
        {
            final BlockPos mloc = te.getMasterBlockLocation();
            final BlockStructure sb = StructureRegistry.getStructureBlock(te.getRegHash());

            if (sb == null || sb.getPattern().getCollisionBoxes() == null)
            {
                return;
            }

            localToGlobalCollisionBoxes(mloc.getX(), mloc.getY(), mloc.getZ(),
                    mask, list, sb.getPattern().getCollisionBoxes(),
                    getOrientation(state), getMirror(state),
                    sb.getPattern().getBlockBounds()
            );
        }
    }

    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TEStructureShape();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer)
    {
        final ITEStructure te = (ITEStructure) world.getTileEntity(pos);

        if (te != null)
        {
            final BlockStructure block = te.getMasterBlockInstance();

            if (block != null)
            {
                return block.addDestroyEffects(world, te.getMasterBlockLocation(), effectRenderer);
            }
        }

        return true; //No Destroy Effects
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        final IBlockState state = world.getBlockState(pos);
        final ITEStructure te = (ITEStructure) world.getTileEntity(pos);
        final boolean isPlayerCreative = player != null && player.capabilities.isCreativeMode;
        final boolean isPlayerSneaking = player != null && player.isSneaking();

        final BlockStructure sb = StructureRegistry.getStructureBlock(te.getRegHash());

        if (sb != null)
        {
            sb.breakStructure(world,
                    te.getMasterBlockLocation(),
                    getOrientation(state),
                    getMirror(state),
                    isPlayerCreative,
                    isPlayerSneaking
            );
            updateExternalNeighbours(world,
                    te.getMasterBlockLocation(),
                    sb.getPattern(),
                    getOrientation(state),
                    getMirror(state),
                    false
            );

        } else
        {
            world.setBlockToAir(pos);
        }

        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float sx, float sy, float sz)
    {
        final TEStructureShape te = (TEStructureShape) world.getTileEntity(pos);

        if (te != null)
        {
            final BlockStructure block = te.getMasterBlockInstance();

            if (block != null)
            {
                return block.onStructureBlockActivated(world, te.getMasterBlockLocation(), player, pos, side, te.getLocal(), sx, sy, sz);
            }
        }

        world.setBlockToAir(pos);

        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        onSharedNeighbourBlockChange(world, pos,
                ((TEStructureShape) world.getTileEntity(pos)).getRegHash(),
                neighborBlock,
                state
        );
    }

    //======================================
    //      V i s u a l   D e b u g
    //======================================

    @Override
    public int getRenderType()
    {
        return _DEBUG?3: -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }
}
