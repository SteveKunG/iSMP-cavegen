package supercoder79.cavegenmod.mixin;

import java.util.Random;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator
{
    @Shadow
    @Final
    protected BiomeSource biomeSource;

    @Redirect(method = "generateStrongholds", at = @At(value = "INVOKE", target = "net/minecraft/world/level/biome/BiomeSource.findBiomeHorizontal(IIIILjava/util/function/Predicate;Ljava/util/Random;)Lnet/minecraft/core/BlockPos;"))
    private BlockPos findY64Biome(BiomeSource source, int x, int y, int z, int radius, Predicate<Biome> predicate, Random random)
    {
        return source.findBiomeHorizontal(x, 64, z, radius, predicate, random);
    }

    @ModifyConstant(method = "applyCarvers", constant = @Constant(intValue = 0))
    private int carveAtY64(int defValue)
    {
        return 64;
    }

    @ModifyConstant(method = "findNearestMapFeature", constant = @Constant(intValue = 32))
    private int setMutableAtY64(int defValue)
    {
        return 64;
    }

    /**
     * @author SuperCoder79
     */
    @Overwrite
    public void applyBiomeDecoration(WorldGenRegion worldGenRegion, StructureFeatureManager structureFeatureManager)
    {
        var chunkPos = worldGenRegion.getCenter();
        var i = chunkPos.getMinBlockX();
        var j = chunkPos.getMinBlockZ();
        var blockPos = new BlockPos(i, worldGenRegion.getMinBuildHeight(), j);
        var biome = this.biomeSource.getNoiseBiome(QuartPos.fromSection(chunkPos.x) + QuartPos.fromBlock(8), 64, QuartPos.fromSection(chunkPos.z) + QuartPos.fromBlock(8));
        var underground = this.biomeSource.getNoiseBiome(QuartPos.fromSection(chunkPos.x) + QuartPos.fromBlock(8), 0, QuartPos.fromSection(chunkPos.z) + QuartPos.fromBlock(8));
        var chunkRandom = new WorldgenRandom();
        var l = chunkRandom.setDecorationSeed(worldGenRegion.getSeed(), i, j);

        try
        {
            biome.generate(structureFeatureManager, (ChunkGenerator) (Object) this, worldGenRegion, l, chunkRandom, blockPos);
            underground.generate(structureFeatureManager, (ChunkGenerator) (Object) this, worldGenRegion, l, chunkRandom, blockPos);
        }
        catch (Exception e)
        {
            CrashReport crashReport = CrashReport.forThrowable(e, "Biome decoration");
            crashReport.addCategory("Generation").setDetail("CenterX", chunkPos.x).setDetail("CenterZ", chunkPos.z).setDetail("Seed", l).setDetail("Biome", biome).setDetail("UndergroundBiome", underground);
            throw new ReportedException(crashReport);
        }
    }
}