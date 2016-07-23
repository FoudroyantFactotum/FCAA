/*
 * Fou's Archive - A Minecraft Mod
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foudroyantfactotum.mod.fousarchive.library;

import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_ShapeBlock;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.FA_StructureBlock;
import com.foudroyantfactotum.mod.fousarchive.blocks.Structure.PlayerPiano.BlockPlayerPiano;

public final class ModBlocks
{
    private ModBlocks()
    {
        //noop
    }

    public static final FA_ShapeBlock structureShape = (FA_ShapeBlock) new FA_ShapeBlock().setUnlocalizedName(ModNames.Blocks.structureShape).setRegistryName(ModNames.Blocks.structureShape);
    public static final FA_StructureBlock playerPiano = (FA_StructureBlock) new BlockPlayerPiano().setUnlocalizedName(ModNames.Blocks.playerPiano).setRegistryName(ModNames.Blocks.playerPiano);
}
