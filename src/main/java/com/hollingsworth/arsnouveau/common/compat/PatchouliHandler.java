package com.hollingsworth.arsnouveau.common.compat;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.registries.ForgeRegistries;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public class PatchouliHandler {

    public static void openBookGUI(ServerPlayer player) {
        PatchouliAPI.get().openBookGUI(player, new ResourceLocation(ArsNouveau.MODID, "worn_notebook"));
    }

    public static void openBookClient(){
        PatchouliAPI.get().openBookGUI(ForgeRegistries.ITEMS.getKey(ItemsRegistry.WORN_NOTEBOOK.asItem()));
    }

    public static boolean isPatchouliWorld() {
        if(!ArsNouveau.patchouliLoaded){
            return false;
        }
        return Minecraft.getInstance().screen instanceof GuiBookEntry;
    }

}
