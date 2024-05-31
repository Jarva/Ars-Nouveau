package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import com.hollingsworth.arsnouveau.setup.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.IForgeRegistry;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryObject;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class ModSetup {

    public static void sendIntercoms() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("an_focus").size(1).icon(new ResourceLocation(Curios.MODID, "slot/empty_curio_slot")).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, MODID);

    public static DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<MagicTrunkPlacer>> MAGIC_TRUNK_PLACER = TRUNK_PLACER_TYPE_DEFERRED_REGISTER.register("magic_trunk_placer", () -> new TrunkPlacerType<>(MagicTrunkPlacer.CODEC));

    public static void registers(IEventBus modEventBus) {
        ItemsRegistry.ITEMS.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        BlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        BlockRegistry.BS_PROVIDERS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModPotions.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        EnchantmentRegistry.ENCHANTMENTS.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        PerkAttributes.ATTRIBUTES.register(modEventBus);
        TRUNK_PLACER_TYPE_DEFERRED_REGISTER.register(modEventBus);
        WorldgenRegistry.FEAT_REG.register(modEventBus);
        LootRegistry.GLM.register(modEventBus);
        SoundRegistry.SOUND_REG.register(modEventBus);
        StructureRegistry.STRUCTURES.register(modEventBus);
        StructureRegistry.STRUCTURE_PROCESSOR.register(modEventBus);

        MenuRegistry.MENU_REG.register(modEventBus);
        VillagerRegistry.POIs.register(modEventBus);
        VillagerRegistry.VILLAGERS.register(modEventBus);
        CreativeTabRegistry.TABS.register(modEventBus);
        DataSerializers.DS.register(modEventBus);
    }

    public static void registerEvents(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK.registryKey())) {
            BlockRegistry.onBlocksRegistry();
        }
        if (event.getRegistryKey().equals(Registries.BLOCK.registryKey())) {
            BlockRegistry.onBlockItemsRegistry();
            ItemsRegistry.onItemRegistry();
        }
    }
}
