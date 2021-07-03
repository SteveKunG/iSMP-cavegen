package supercoder79.cavegenmod.mixin.structurefix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;

@Mixin(NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class)
public class MixinNetherBridgePieces_CastleSmallCorridorLeftTurnPiece
{
    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/NetherBridgePieces$CastleSmallCorridorLeftTurnPiece.isNeedingChest:Z", ordinal = 0))
    private boolean changeTrue(NetherBridgePieces.CastleSmallCorridorLeftTurnPiece piece)
    {
        return true;
    }
}