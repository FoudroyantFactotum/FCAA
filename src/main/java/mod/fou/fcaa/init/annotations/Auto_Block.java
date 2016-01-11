package mod.fou.fcaa.init.annotations;

import mod.fou.fcaa.init.InitBlock;

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
}
