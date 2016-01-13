package mod.fou.fcaa.Blocks;

import mod.fou.fcaa.TheMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;


public abstract class FCAA_Block extends Block
{
    public FCAA_Block(Material materialIn)
    {
        super(materialIn);
    }

    @Override
    public String getUnlocalizedName()
    {
        final String unloc = super.getUnlocalizedName();

        return "tile." + TheMod.MOD_ID + ":" + unloc.substring(unloc.indexOf('.') + 1);
    }
}
