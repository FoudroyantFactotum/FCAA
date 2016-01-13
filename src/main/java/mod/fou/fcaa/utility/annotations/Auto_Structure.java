package mod.fou.fcaa.utility.annotations;

import mod.fou.fcaa.Blocks.Structure.TEStructure;
import mod.fou.fcaa.init.InitBlock;
import mod.fou.fcaa.structure.ItemBlockStructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Auto_Structure
{
    String name();

    Class<? extends TEStructure> tileEntity();

    String tab() default InitBlock.ModTab.main;

    Class<? extends ItemBlockStructure> item() default ItemBlockStructure.class;
}
