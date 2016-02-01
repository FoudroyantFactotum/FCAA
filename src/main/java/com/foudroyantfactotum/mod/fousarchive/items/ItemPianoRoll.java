package com.foudroyantfactotum.mod.fousarchive.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemPianoRoll extends FA_Item
{
    public static ItemPianoRoll INSTANCE = null;

    public static final String SONG_NAME = "songName";
    private static final List<ResourceLocation> allSongs = new ArrayList<>();

    public static void addPianoRoll(ResourceLocation rl)
    {
        allSongs.add(rl);
    }

    public static ResourceLocation getPianoRoll(int index)
    {
        if (index > -1 && index < allSongs.size())
            return allSongs.get(index);

        return null;
    }

    public ItemPianoRoll()
    {
        setUnlocalizedName("pianoRoll");
        setMaxStackSize(1);
        setNoRepair();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }

    @Override
    public int getMaxDamage()
    {
        return allSongs.size();
    }

    @Override
    public boolean isItemTool(ItemStack stack)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        tooltip.clear();

        final int dam = stack.getItemDamage();

        tooltip.add(getPianoRoll(dam).toString());
    }

    @Override
    public String getHighlightTip(ItemStack item, String displayName)
    {
        final ResourceLocation rl = getPianoRoll(item.getItemDamage());

        return rl == null ? displayName : rl.toString();
    }
}
