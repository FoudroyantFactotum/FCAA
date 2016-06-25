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
