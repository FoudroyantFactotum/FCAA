package mod.fou.fcaa.utility.annotations;

import mod.fou.fcaa.init.InitBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Auto_Block
{
    String name();

    String tab() default InitBlock.ModTab.main;

    Class<? extends TileEntity> tileEntity() default TileEntity.class;

    Class<? extends ItemBlock> item() default ItemBlock.class;
}
