package supercoder79.cavegenmod.mixin.particle;

import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level
{
    MixinClientLevel()
    {
        super(null, null, null, null, false, false, 0);
    }

    @Inject(method = "doAnimateTick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addSporeInLushCave(int i, int j, int k, int l, Random random, @Nullable @Coerce Object markerParticleStatus, BlockPos.MutableBlockPos mutableBlockPos, CallbackInfo info, int m, int n, int o, BlockState blockState)
    {
        var biomeKey = this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(this.getBiome(mutableBlockPos));

        if (!blockState.isCollisionShapeFullBlock(this, mutableBlockPos) && Biomes.LUSH_CAVES.location().equals(biomeKey) && random.nextFloat() <= 0.00625F)
        {
            this.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, mutableBlockPos.getX() + this.random.nextDouble(), mutableBlockPos.getY() + this.random.nextDouble(), mutableBlockPos.getZ() + this.random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }
    }
}