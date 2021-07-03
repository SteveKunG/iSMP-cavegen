package supercoder79.cavegenmod.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.levelgen.SimpleRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.layer.Layer;

@Mixin(OverworldBiomeSource.class)
public class MixinOverworldBiomeSource
{
    @Shadow
    @Final
    Layer noiseBiomeLayer;

    @Shadow
    @Final
    Registry<Biome> biomes;

    @Unique
    private ImprovedNoise lushCavesNoise;

    @Unique
    private ImprovedNoise dripstoneCavesNoise;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void makeNoise(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes, Registry<Biome> biomeRegistry, CallbackInfo info)
    {
        this.lushCavesNoise = new ImprovedNoise(new SimpleRandomSource(seed));
        this.dripstoneCavesNoise = new ImprovedNoise(new SimpleRandomSource(seed + 69420));
    }

    private Biome getBiomeFromKey(ResourceKey<Biome> key)
    {
        var biome = this.biomes.get(key);

        if (biome == null)
        {
            Util.logAndPauseIfInIde("Cave biome surface checker biome ID missing");
            return this.biomes.get(net.minecraft.data.worldgen.biome.Biomes.byId(0));
        }
        else
        {
            return biome;
        }
    }

    private List<Biome> getBiomesFromKeys(List<ResourceKey<Biome>> keys)
    {
        var ret = Lists.<Biome>newArrayList();

        for (ResourceKey<Biome> key : keys)
        {
            ret.add(this.getBiomeFromKey(key));
        }
        return ret;
    }

    /**
     * @author SuperCoder79 (initial code and the bulk of the mixin work)
     * @author ex0planetary (noise work - 3D, more Mojang-like cave biomes)
     */
    @Overwrite
    public Biome getNoiseBiome(int biomeX, int biomeY, int biomeZ)
    {
        var defaultBiome = this.noiseBiomeLayer.get(this.biomes, biomeX, biomeZ);
        var isOceanBiome = defaultBiome.getBiomeCategory() == Biome.BiomeCategory.OCEAN;
        var isDeepOceanBiome = isOceanBiome && this.getBiomesFromKeys(ImmutableList.of(Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_WARM_OCEAN)).contains(defaultBiome);
        var isHighLushBiome = this.getBiomesFromKeys(ImmutableList.of(Biomes.FOREST, Biomes.WOODED_HILLS, Biomes.FLOWER_FOREST, Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS)).contains(defaultBiome) || defaultBiome.getBiomeCategory() == Biome.BiomeCategory.JUNGLE;
        var isLowLushHighDripBiome = ImmutableList.of(Biome.BiomeCategory.EXTREME_HILLS, Biome.BiomeCategory.MESA, Biome.BiomeCategory.DESERT, Biome.BiomeCategory.ICY).contains(defaultBiome.getBiomeCategory());
        var fullyUndergroundY = isOceanBiome ? isDeepOceanBiome ? 5 : 8 : 11;
        var partiallyUndergroundY = fullyUndergroundY + 2;
        //        var lushThreshold = isHighLushBiome ? 0.39F : (isLowLushHighDripBiome ? 0.45F : 0.42F);
        var lushThreshold = isHighLushBiome ? 0.10F : isLowLushHighDripBiome ? 0.22F : 0.18F;
        //        var dripThreshold = isLowLushHighDripBiome ? 0.39F : 0.42F;
        var dripThreshold = isLowLushHighDripBiome ? 0.08F : 0.16F;
        //        var lushFreq = isHighLushBiome ? 50.0D : (isLowLushHighDripBiome ? 80.0D : 65.0D);
        var lushFreq = isHighLushBiome ? 40.0D : isLowLushHighDripBiome ? 60.0D : 45.0D;
        //        var dripFreq = isLowLushHighDripBiome ? 50.0D : 65.0D;
        var dripFreq = isLowLushHighDripBiome ? 20.0D : 30.0D;

        if (biomeY < fullyUndergroundY)
        {
            // fully underground, no need to worry about gradient shit
            if (this.lushCavesNoise.noise(biomeX / lushFreq, biomeY / 10.0, biomeZ / lushFreq) > lushThreshold)
            {
                return this.biomes.get(Biomes.LUSH_CAVES);
            }
            else if (this.dripstoneCavesNoise.noise(biomeX / dripFreq, biomeY / 15.0, biomeZ / dripFreq) > dripThreshold)
            {
                return this.biomes.get(Biomes.DRIPSTONE_CAVES);
            }
        }
        else if (biomeY < partiallyUndergroundY)
        {
            // not fully underground - multiply by normalized gradient from 1 at biomeY = 11 to 0 at biomeY = 13 (or other values if it's an ocean lol)
            if (this.lushCavesNoise.noise(biomeX / lushFreq, biomeY / 10.0, biomeZ / lushFreq) * (biomeY - fullyUndergroundY) / 2 > lushThreshold)
            {
                return this.biomes.get(Biomes.LUSH_CAVES);
            }
            else if (this.dripstoneCavesNoise.noise(biomeX / dripFreq, biomeY / 15.0, biomeZ / dripFreq) * (biomeY - fullyUndergroundY) / 2 > dripThreshold)
            {
                return this.biomes.get(Biomes.DRIPSTONE_CAVES);
            }
        }
        return defaultBiome;
    }
}