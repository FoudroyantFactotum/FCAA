package com.foudroyantfactotum.mod.fousarchive.textures;

public class Generator
{
    public static void init()
    {
        generateTextures();
    }

    private static final int noOfTextures = 2;

    private static void generateTextures()
    {
        /*final ItemModelMesher imm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        for (int i=0; i < noOfTextures; ++i)
        {
            final ResourceLocation rlbf = new ResourceLocation(TheMod.MOD_ID, "textures/RollIcon" + i + ".png");
            BufferedImage bf;

            try (final InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rlbf).getInputStream())
            {
                bf = TextureUtil.readBufferedImage(is);
            } catch (IOException e)
            {
                continue;
            }

            for (int ii=0; ii < genColours.length; ++ii)
            {
                final ResourceLocation rl = new ResourceLocation(TheMod.MOD_ID, "generated/RollIcon" + i + "-" + ii);
                final TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(rl);

                final BufferedImage sbf = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

                for (int y = 0; y < sbf.getHeight(); ++y)
                    for (int x = 0; x < sbf.getWidth(); ++x)
                        if (bf.getRGB(x, y) == matchColor.getRGB())
                            sbf.setRGB(x, y, genColours[ii]);
                        else
                            sbf.setRGB(x, y, bf.getRGB(x, y));

                try
                {
                    sprite.loadSprite(new BufferedImage[]{sbf}, null);
                } catch (IOException ignored) {}

                imm.register(ItemPianoRoll.INSTANCE, 0, );
            }
        }*/
    }
}
