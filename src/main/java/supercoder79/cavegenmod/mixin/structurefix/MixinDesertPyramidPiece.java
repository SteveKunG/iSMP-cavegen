package supercoder79.cavegenmod.mixin.structurefix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;

@Mixin(DesertPyramidPiece.class)
public class MixinDesertPyramidPiece
{
    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/DesertPyramidPiece.hasPlacedChest:[Z", ordinal = 0))
    private boolean changeTrue(boolean[] hasPlacedChest, int index)
    {
        return false;
    }
}