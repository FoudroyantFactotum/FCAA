package com.foudroyantfactotum.mod.fousarchive.blocks.Structure;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.tool.structure.block.StructureBlock;

public abstract class FA_StructureBlock extends StructureBlock
{
    @Override
    public String getUnlocalizedName()
    {
        final String unloc = super.getUnlocalizedName();

        return "tile." + TheMod.MOD_ID + ":" + unloc.substring(unloc.indexOf('.') + 1);
    }
}
