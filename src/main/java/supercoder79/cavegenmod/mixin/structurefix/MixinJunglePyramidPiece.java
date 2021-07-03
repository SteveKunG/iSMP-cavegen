package supercoder79.cavegenmod.mixin.structurefix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;

@Mixin(JunglePyramidPiece.class)
public class MixinJunglePyramidPiece
{
    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/JunglePyramidPiece.placedHiddenChest:Z", ordinal = 0))
    private boolean changeTrue1(JunglePyramidPiece piece)
    {
        return false;
    }

    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/JunglePyramidPiece.placedMainChest:Z", ordinal = 0))
    private boolean changeTrue2(JunglePyramidPiece piece)
    {
        return false;
    }

    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/JunglePyramidPiece.placedTrap1:Z", ordinal = 0))
    private boolean changeTrue3(JunglePyramidPiece piece)
    {
        return false;
    }

    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/JunglePyramidPiece.placedTrap2:Z", ordinal = 0))
    private boolean changeTrue4(JunglePyramidPiece piece)
    {
        return false;
    }
}