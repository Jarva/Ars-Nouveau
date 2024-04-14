package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientDelayEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class EffectDelay extends AbstractEffect {
    public static EffectDelay INSTANCE = new EffectDelay();

    private EffectDelay() {
        super(GlyphLib.EffectDelayID, "Delay");
    }

    public void sendPacket(Level world, HitResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats spellStats, BlockHitResult blockResult, Entity hitEntity) {
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        Spell newSpell = spellContext.getRemainingSpell();
        SpellContext newContext = spellContext.clone().withSpell(newSpell);
        spellContext.setCanceled(true);
        int duration = GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getBuffCount(AugmentExtendTime.INSTANCE) * 20;
        int decreasedTime = EXTEND_TIME.get() * 10 * spellStats.getBuffCount(AugmentDurationDown.INSTANCE);
        duration -= decreasedTime;
<<<<<<< Updated upstream
=======
        if (spellStats.hasBuff(AugmentRandomize.INSTANCE)) {
            double randomize = spellStats.getBuffCount(AugmentRandomize.INSTANCE) * RANDOMIZE_CHANCE.get();
            duration = world.random.nextIntBetweenInclusive((int) (duration * (1 - randomize)), (int) (duration * (1 + randomize)));
        }
>>>>>>> Stashed changes
        EventQueue.getServerInstance().addEvent(
                new DelayedSpellEvent(duration, rayTraceResult, world, newContext));
        Networking.sendToNearby(world, BlockPos.containing(safelyGetHitPos(rayTraceResult)),
                new PacketClientDelayEffect(duration, shooter, newSpell, newContext, blockResult, hitEntity));
    }


    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        sendPacket(world, rayTraceResult, shooter, spellContext, spellStats, rayTraceResult, null);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        sendPacket(world, rayTraceResult, shooter, spellContext, spellStats, null, rayTraceResult.getEntity());
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 1);
        addRandomizeConfig(builder, 0.25f);
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
