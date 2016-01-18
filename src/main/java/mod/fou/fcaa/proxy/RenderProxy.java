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
package mod.fou.fcaa.proxy;

import mod.fou.fcaa.Blocks.FCAA_TE;
import mod.fou.fcaa.Blocks.Structure.FCAA_TESR;
import net.minecraft.block.Block;

public class RenderProxy
{
    public void preInit() { }

    public void init() { }

    public <E extends FCAA_TE> void registerTESR(Class<E> te, FCAA_TESR<E> tesr) { }

    public void registerBlockAsItemModel(Block block) { }
}
