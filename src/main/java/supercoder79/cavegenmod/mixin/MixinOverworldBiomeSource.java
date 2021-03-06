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

    @Inject(method = "<init>", at = @At("TAIL"))
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
        var biome = this.noiseBiomeLayer.get(this.biomes, biomeX, biomeZ);
        var isOceanBiome = biome.getBiomeCategory() == Biome.BiomeCategory.OCEAN;
        var isDeepOceanBiome = isOceanBiome && this.getBiomesFromKeys(ImmutableList.of(Biomes.DEEP_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_WARM_OCEAN)).contains(biome);
        var isHighLushBiome = this.getBiomesFromKeys(ImmutableList.of(Biomes.FOREST, Biomes.WOODED_HILLS, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS)).contains(biome) || biome.getBiomeCategory() == Biome.BiomeCategory.JUNGLE || biome.getBiomeCategory() == Biome.BiomeCategory.MUSHROOM;
        var isLowLushHighDripBiome = ImmutableList.of(Biome.BiomeCategory.EXTREME_HILLS, Biome.BiomeCategory.TAIGA, Biome.BiomeCategory.MESA, Biome.BiomeCategory.DESERT, Biome.BiomeCategory.ICY, Biome.BiomeCategory.SAVANNA, Biome.BiomeCategory.SWAMP).contains(biome.getBiomeCategory());
        var fullyUndergroundY = isOceanBiome ? isDeepOceanBiome ? 5 : 8 : 11;
        var partiallyUndergroundY = fullyUndergroundY + 2;
        var lushThreshold = isHighLushBiome ? 0.1F : isLowLushHighDripBiome ? 0.2F : 0.16F;
        var dripThreshold = isLowLushHighDripBiome ? -0.1F : 0.12F;
        var lushFreq = isHighLushBiome ? 60.0D : isLowLushHighDripBiome ? 40.0D : 50.0D;
        var dripFreq = isLowLushHighDripBiome ? 100.0D : 30.0D;
        var lushNoise = this.lushCavesNoise.noise(biomeX / lushFreq, biomeY / 10.0, biomeZ / lushFreq);
        var dripNoise = this.dripstoneCavesNoise.noise(biomeX / dripFreq, biomeY / 15.0, biomeZ / dripFreq);

        if (biomeY < fullyUndergroundY)
        {
            // fully underground, no need to worry about gradient shit
            if (lushNoise > lushThreshold)
            {
                return this.biomes.get(Biomes.LUSH_CAVES);
            }
            else if (dripNoise > dripThreshold)
            {
                return this.biomes.get(Biomes.DRIPSTONE_CAVES);
            }
        }
        else if (biomeY < partiallyUndergroundY)
        {
            // not fully underground - multiply by normalized gradient from 1 at biomeY = 11 to 0 at biomeY = 13 (or other values if it's an ocean lol)
            if (lushNoise / 2 > lushThreshold)
            {
                return this.biomes.get(Biomes.LUSH_CAVES);
            }
            else if (dripNoise * (biomeY - fullyUndergroundY) / 2 > dripThreshold)
            {
                return this.biomes.get(Biomes.DRIPSTONE_CAVES);
            }
        }
        return biome;
    }
}