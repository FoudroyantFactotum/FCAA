package com.foudroyantfactotum.mod.fousarchive.items;

import net.minecraft.util.ResourceLocation;

public class ItemPianoRoll extends FA_Item
{
    public static final String SONG_NAME = "songName";

    private ResourceLocation songResource;

    public ItemPianoRoll(ResourceLocation songResource)
    {
        this.songResource = songResource;
        setMaxStackSize(1);
    }

    public ResourceLocation getSongResource()
    {
        return songResource;
    }

    @Override
    public boolean isDamageable()
    {
        return false;
    }
}
