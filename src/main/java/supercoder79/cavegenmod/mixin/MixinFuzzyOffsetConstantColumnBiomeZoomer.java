package supercoder79.cavegenmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FuzzyOffsetBiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetConstantColumnBiomeZoomer;

@Mixin(FuzzyOffsetConstantColumnBiomeZoomer.class)
public class MixinFuzzyOffsetConstantColumnBiomeZoomer
{
    /**
     * @author SuperCoder79
     */
    @Overwrite
    public Biome getBiome(long seed, int x, int y, int z, BiomeManager.NoiseBiomeSource noiseBiomeSource)
    {
        return FuzzyOffsetBiomeZoomer.INSTANCE.getBiome(seed, x, y, z, noiseBiomeSource);
    }
}