package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.library.ModItems;
import com.foudroyantfactotum.mod.fousarchive.loot.LootPianoRollNBT;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

public enum InitLootSys
{
    INSTANCE;

    final LootCondition[] lc = new LootCondition[0];
    final LootFunction lf = new LootPianoRollNBT(lc);
    final LootPool poolPianoRolls = new LootPool(new LootEntry[]{
            new LootEntryItem(ModItems.pianoRoll, 1, 1, new LootFunction[]{lf}, lc, TheMod.MOD_ID + "-pianoRolls")
    }, lc, new RandomValueRange(0.0f, 1.0f), new RandomValueRange(0.4f), TheMod.MOD_ID);

    public void init()
    {
        LootFunctionManager.registerFunction(new LootPianoRollNBT.Serializer());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void lootTableLoad(LootTableLoadEvent event)
    {
        if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) ||
                event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH) ||
                event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST) ||
                event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) ||
                event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE) ||
                event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY) ||
                event.getName().equals(LootTableList.GAMEPLAY_FISHING_TREASURE) ||
                event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID) ||
                event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)
                )
        {
            Optional.of(event.getTable()).ifPresent(
                    lootTable -> lootTable.addPool(poolPianoRolls)
            );
        }
    }
}
