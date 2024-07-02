package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ISpellHotkeyListener {

    default void onNextKeyPressed(ItemStack stack, ServerPlayer player) {
        ISpellCaster iSpellCaster = SpellCasterRegistry.from(stack);
        iSpellCaster.setNextSlot();
    }

    default void onPreviousKeyPressed(ItemStack stack, ServerPlayer player) {
        ISpellCaster iSpellCaster = SpellCasterRegistry.from(stack);
        iSpellCaster.setPreviousSlot();
    }

    default void onQuickCast(ItemStack stack, ServerPlayer player, InteractionHand hand, int slot){
        ISpellCaster iSpellCaster = SpellCasterRegistry.from(stack);
        iSpellCaster.castSpell(player.level, player, hand, null, iSpellCaster.getSpell(slot));
    }
    //TODO: 1.20 Remove this and have onQuickCast return a boolean
    @Deprecated(forRemoval = true)
    default boolean canQuickCast(){
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
    }
}
