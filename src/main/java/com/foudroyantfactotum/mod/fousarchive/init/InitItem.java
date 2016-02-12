package com.foudroyantfactotum.mod.fousarchive.init;

import com.foudroyantfactotum.mod.fousarchive.items.FA_Item;
import com.foudroyantfactotum.mod.fousarchive.utility.Clazz;
import com.foudroyantfactotum.mod.fousarchive.utility.annotations.Auto_Item;
import com.google.common.reflect.ClassPath;
import net.minecraft.item.Item;
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
                        GameRegistry.registerItem(item, item.getUnlocalizedName());

                        if (!InitBlock.ModTab.none.equals(annot.tab()))//todo better error handling
                            item.setCreativeTab(InitBlock.ModTab.tabs.get(annot.tab()));
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)//todo better errors
                {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
