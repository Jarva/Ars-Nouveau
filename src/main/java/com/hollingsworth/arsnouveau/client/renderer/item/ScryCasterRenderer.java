package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ScryCaster;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ScryCasterRenderer extends FixedGeoItemRenderer<ScryCaster>{
    public ScryCasterRenderer() {
        super(new AnimatedGeoModel<ScryCaster>() {
            @Override
            public ResourceLocation getModelResource(ScryCaster wand) {
                return new ResourceLocation(ArsNouveau.MODID, "geo/enchanters_eye.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(ScryCaster wand) {
                return new ResourceLocation(ArsNouveau.MODID, "textures/items/enchanters_eye.png");
            }

            @Override
            public ResourceLocation getAnimationResource(ScryCaster wand) {
                return new ResourceLocation(ArsNouveau.MODID, "animations/enchanters_eye.json");
            }
        });
    }
}
