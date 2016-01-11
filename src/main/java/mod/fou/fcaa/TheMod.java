package mod.fou.fcaa;

import mod.fou.fcaa.init.InitBlock;
import mod.fou.fcaa.utility.Log.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TheMod.MOD_ID, name = TheMod.MOD_NAME, version = TheMod.MOD_VERSION, useMetadata = true)
public class TheMod
{
    public static final String MOD_ID = "fcaa";
    public static final String MOD_NAME = "Fou's Conservatory of Arcane Apparatuses";
    public static final String MOD_VERSION = "0.1";

    @Mod.Instance
    public static TheMod instance;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Logger.info("==========preInit==========");
        InitBlock.init();
    }
}
