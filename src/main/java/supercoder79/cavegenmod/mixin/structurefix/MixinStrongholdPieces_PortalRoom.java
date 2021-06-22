package supercoder79.cavegenmod.mixin.structurefix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;

@Mixin(StrongholdPieces.PortalRoom.class)
public class MixinStrongholdPieces_PortalRoom
{
    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/StrongholdPieces$PortalRoom.hasPlacedSpawner:Z", ordinal = 0))
    private boolean changeTrue(StrongholdPieces.PortalRoom portalRoom)
    {
        return false;
    }
}