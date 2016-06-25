package com.foudroyantfactotum.mod.fousarchive.library;

import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.items.ItemTuningFork;
import net.minecraft.item.Item;

public final class ModItems
{
    private ModItems()
    {
        //noop
    }

    public static final Item pianoRoll = new ItemPianoRoll().setUnlocalizedName(ModNames.Items.pianoRoll).setRegistryName(ModNames.Items.pianoRoll);
    public static final Item tuningFork = new ItemTuningFork().setUnlocalizedName(ModNames.Items.tuningFork).setRegistryName(ModNames.Items.tuningFork);
}
