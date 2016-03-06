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
package com.foudroyantfactotum.mod.fousarchive.utility.annotations;

import com.foudroyantfactotum.mod.fousarchive.TESR.FA_TESR;
import com.foudroyantfactotum.mod.fousarchive.init.InitBlock;
import com.foudroyantfactotum.tool.structure.item.StructureBlockItem;
import com.foudroyantfactotum.tool.structure.tileentity.StructureTE;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Auto_Structure
{
    String name();

    Class<? extends StructureTE> tileEntity();

    String tab() default InitBlock.ModTab.main;

    Class<? extends StructureBlockItem> item() default StructureBlockItem.class;

    @SideOnly(Side.CLIENT)
    Class<? extends FA_TESR> TESR() default FA_TESR.class;
}
