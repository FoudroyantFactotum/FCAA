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
package com.foudroyantfactotum.mod.fousarchive.loot;

import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class LootPianoRollNBT extends LootFunction
{
    public LootPianoRollNBT(LootCondition[] conditionsIn)
    {
        super(conditionsIn);
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context)
    {
        final NBTTagCompound nnbt = new NBTTagCompound();
        final String name = ItemPianoRoll.getPianoRoll((int) (rand.nextDouble()*ItemPianoRoll.getPianoRollCount())).toString();

        ItemPianoRoll.setPianoRollNBT(nnbt, name);

        stack.setItemDamage(Math.abs(name.hashCode()) % ItemPianoRoll.iconNo);
        stack.setTagCompound(nnbt);

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<LootPianoRollNBT>
    {
        public Serializer()
        {
            super(new ResourceLocation("Piano-Roll-NBT"), LootPianoRollNBT.class);
        }

        @Override
        public void serialize(JsonObject object, LootPianoRollNBT functionClazz, JsonSerializationContext serializationContext)
        {
            //noop
        }

        @Override
        public LootPianoRollNBT deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn)
        {
            return new LootPianoRollNBT(conditionsIn);
        }
    }
}
