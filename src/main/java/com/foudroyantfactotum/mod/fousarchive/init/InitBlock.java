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
package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.blocks.FA_Block;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_ShapeBlock;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_TESR;
import com.foudroyantfactotum.mod.fousarchive.utility.Clazz;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Block;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Instance;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Ore;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Structure;
import com.foudroyantfactotum.tool.structure.StructureRegistry;
import com.foudroyantfactotum.tool.structure.block.StructureBlock;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class InitBlock
{
    public static class ModTab
    {
        public static final ImmutableMap <String, CreativeTabs> tabs;

        public static final String main = "main";
        public static final String none = "none"; //no tab

         static
         {
            ImmutableMap.Builder<String, CreativeTabs> builder = ImmutableMap.builder();

            builder.put(main, new CreativeTabs(TheMod.MOD_ID)
            {
                @Override
                public Item getTabIconItem()
                {
                    return Items.apple;
                }
            });

             tabs = builder.build();
        }
    }

    public static void init()
    {
        registerTaggedBlocks();
        registerBlocks();
        registerTaggedStructures();
        registerStructures();
    }

    private static void registerBlocks()
    {

    }

    private static void registerStructures()
    {

    }

    private static void registerTaggedBlocks()
    {
        try
        {
            final Field mField = Field.class.getDeclaredField("modifiers");
            mField.setAccessible(true);

            for (final ClassPath.ClassInfo i : Clazz.getClassListFrom(FA_Block.class.getPackage()))
            {
                try
                {
                    final Class<?> clazz = Class.forName(i.getName());
                    final Auto_Block annot = clazz.getAnnotation(Auto_Block.class);

                    if (annot != null)
                    {
                        final Field fINSTANCE = getInstanceField(clazz);
                        final Block block = (Block) clazz.newInstance();

                        fINSTANCE.setAccessible(true);
                        mField.setInt(fINSTANCE, fINSTANCE.getModifiers() & ~Modifier.FINAL);
                        fINSTANCE.set(null, block);

                        block.setUnlocalizedName(annot.name());
                        GameRegistry.registerBlock(block, annot.item(), annot.name());//consider null items?

                        if (!ModTab.none.equals(annot.tab()))//todo better error handling
                            block.setCreativeTab(ModTab.tabs.get(annot.tab()));

                        if (annot.tileEntity() != TileEntity.class) //TileEntity class use as default null
                            GameRegistry.registerTileEntity(annot.tileEntity(), "tile." + annot.name());

                        if (clazz.isAnnotationPresent(Auto_Ore.class))
                            OreDictionary.registerOre(annot.name(), block);

                        TheMod.render.registerBlockAsItemModel(block);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)//todo better errors
                {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    private static void registerTaggedStructures()
    {
        try
        {
            final Field mField = Field.class.getDeclaredField("modifiers");
            mField.setAccessible(true);

            for (final ClassPath.ClassInfo i : Clazz.getClassListFrom(FA_ShapeBlock.class.getPackage()))
            {
                try
                {
                    final Class<?> clazz = Class.forName(i.getName());
                    final Auto_Structure annot = clazz.getAnnotation(Auto_Structure.class);

                    if (annot != null)
                    {
                        final Field fINSTANCE = getInstanceField(clazz);
                        final StructureBlock block = (StructureBlock) clazz.newInstance();

                        fINSTANCE.setAccessible(true);
                        mField.setInt(fINSTANCE, fINSTANCE.getModifiers() & ~Modifier.FINAL);
                        fINSTANCE.set(null, block);

                        block.setUnlocalizedName(annot.name());
                        GameRegistry.registerBlock(block, annot.item(), annot.name());//consider null items?
                        GameRegistry.registerTileEntity(annot.tileEntity(), "tile." + annot.name());

                        StructureRegistry.registerStructureForLoad(block, FA_ShapeBlock.INSTANCE);

                        if (!ModTab.none.equals(annot.tab()))//todo better error handling
                            block.setCreativeTab(ModTab.tabs.get(annot.tab()));

                        if (annot.TESR() != FA_TESR.class)
                            TheMod.render.registerTESR(annot.tileEntity(), annot.TESR().newInstance());

                        TheMod.render.registerBlockAsItemModel(block);
                    }
                } catch (ClassNotFoundException e)//todo better errors
                {
                    e.printStackTrace();
                } catch (InstantiationException e)
                {
                    e.printStackTrace();
                } catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static Field getInstanceField(Class<?> clazz)
    {
        for (Field f : clazz.getFields())
        {
            final Auto_Instance aINSTANCE = f.getAnnotation(Auto_Instance.class);

            if (aINSTANCE != null) //found Annotation
            {
                final int mod = f.getModifiers();

                if (Modifier.isFinal(mod) && Modifier.isPublic(mod) && Modifier.isStatic(mod))
                {
                    return f;
                }
            }
        }

        return null; //todo fix
    }

}
