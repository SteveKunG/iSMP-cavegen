package supercoder79.cavegenmod.mixin.structurefix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.structure.MineShaftPieces;

@Mixin(MineShaftPieces.MineShaftCorridor.class)
public class MixinMineShaftPieces_MineShaftCorridor
{
    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/MineShaftPieces$MineShaftCorridor.hasPlacedSpider:Z", ordinal = 0))
    private boolean changeTrue(MineShaftPieces.MineShaftCorridor corridor)
    {
        return false;
    }
}