package com.hollingsworth.arsnouveau.common.mixin.weather;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.util.WeatherUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

public class WeatherMixins {
    @Mixin(ServerLevel.class)
    public abstract static class ServerLevelMixin extends Level {
        protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
            super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
        }

        @WrapOperation(method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
        )
        public boolean tickChunk(ServerLevel instance, Operation<Boolean> original, @Local ChunkPos chunkPos) {
            BlockPos blockPos = chunkPos.getWorldPosition();
            return isRainingAt(blockPos) || original.call(instance);
        }
    }

    @Mixin(Level.class)
    public static class LevelMixin {
        private Level getLevel() {
            return (Level) (Object) this;
        }

        @Inject(method = "isRainingAt", at = @At(value = "HEAD"), cancellable = true)
        public void isRainingAt(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(WeatherUtil.isRainingAt(this.getLevel(), pos));
        }

        @Inject(method = "getRainLevel", at = @At(value = "HEAD"), cancellable = true)
        public void getRainLevel(float pDelta, CallbackInfoReturnable<Float> cir) {
            cir.setReturnValue(1.0f);
        }
    }

    @Mixin(ClientLevel.class)
    public abstract static class ClientLevelMixin extends Level {
        protected ClientLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
            super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
        }

        @Override
        public float getRainLevel(float pDelta) {
            Player player = ArsNouveau.proxy.getPlayer();
            if (player != null && WeatherUtil.isRainingAt(player.level(), player.blockPosition())) {
                return 1.0f;
            }
            return super.getRainLevel(pDelta);
        }
    }

    @Mixin(ClientLevel.ClientLevelData.class)
    public static class ClientLevelDataMixin {
        @Inject(method = "isRaining", at = @At(value = "HEAD"), cancellable = true)
        public void isRaining(CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(true);
        }
    }

    @Mixin(LevelRenderer.class)
    public static class LevelRendererMixin {
        @WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
        public Biome.Precipitation getRainLevel(Biome instance, BlockPos pPos, Operation<Biome.Precipitation> original) {
            if (WeatherUtil.isRainingAt(ArsNouveau.proxy.getClientWorld(), pPos)) {
                return original.call(instance, pPos);
            }
            return Biome.Precipitation.NONE;
        }

        @WrapOperation(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
        public void getRainLevel(ClientLevel instance, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Operation<Void> original) {
            if (WeatherUtil.isRainingAt(ArsNouveau.proxy.getClientWorld(), new BlockPos((int) pX, (int) pY, (int) pZ))) {
                original.call(instance, pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            }
        }

        @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
        public float getRainLevel(ClientLevel instance, float v, Operation<Float> original) {
            Player player = ArsNouveau.proxy.getPlayer();
            if (player != null) {
                return WeatherUtil.isRainingAt(player.level(), player.blockPosition()) ? 1.0f : 0.0f;
            }
            return original.call(instance, v);
        }
    }
}
