package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.TheMod;
import com.foudroyantfactotum.mod.fousarchive.items.FA_Item;
import com.foudroyantfactotum.mod.fousarchive.items.ItemPianoRoll;
import com.foudroyantfactotum.mod.fousarchive.proxy.ClientRenderProxy;
import com.foudroyantfactotum.mod.fousarchive.utility.Clazz;
import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Item;
import com.google.common.reflect.ClassPath;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.foudroyantfactotum.mod.fousarchive.init.InitBlock.getInstanceField;

public class InitItem
{
    public static void init()
    {
        registerTaggedItems();
        registerItems();
    }

    private static void registerItems()
    {
        if (TheMod.proxy instanceof ClientRenderProxy)
        {
            registerSpecialPianoRollIcons();
        }
    }

    private static void registerSpecialPianoRollIcons()
    {
        TheMod.proxy.registerItemVariants(ItemPianoRoll.INSTANCE,
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll0"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll1"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll2"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll3"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll4"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll5"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll6"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll7"),
                new ResourceLocation(TheMod.MOD_ID, "pianoRoll8")
        );

        for (int i = 0; i < ItemPianoRoll.iconNo; ++i)
        {
            final ResourceLocation rl = new ResourceLocation(TheMod.MOD_ID, "pianoRoll"+i);

            TheMod.proxy.registerMetaItemModel(ItemPianoRoll.INSTANCE, i,
                            new ModelResourceLocation(rl, "inventory")
                    );
        }
    }

    private static void registerTaggedItems()
    {
        try
        {
            final Field mField = Field.class.getDeclaredField("modifiers");
            mField.setAccessible(true);

            for (final ClassPath.ClassInfo i : Clazz.getClassListFrom(FA_Item.class.getPackage()))
            {
                try
                {
                    final Class<?> clazz = Class.forName(i.getName());
                    final Auto_Item annot = clazz.getAnnotation(Auto_Item.class);

                    if (annot != null)
                    {
                        final Field fINSTANCE = getInstanceField(clazz);
                        final Item item = (Item) clazz.newInstance();

                        fINSTANCE.setAccessible(true);
                        mField.setInt(fINSTANCE, fINSTANCE.getModifiers() & ~Modifier.FINAL);
                        fINSTANCE.set(null, item);

                        item.setUnlocalizedName(annot.name());
                        GameRegistry.registerItem(item, annot.name());

                        if (!InitBlock.ModTab.none.equals(annot.tab()))//todo better error handling
                            item.setCreativeTab(InitBlock.ModTab.tabs.get(annot.tab()));
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError e)//todo better errors
                {
                    throw new FousArchiveException("Error on " + i.getName(), e);
                }
            }
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
