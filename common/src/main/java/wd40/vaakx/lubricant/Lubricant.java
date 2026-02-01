package wd40.vaakx.lubricant;

import net.minecraft.resources.ResourceLocation;

public final class Lubricant {
    public static final String MOD_ID = "lubricant";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private Lubricant() {}
}
