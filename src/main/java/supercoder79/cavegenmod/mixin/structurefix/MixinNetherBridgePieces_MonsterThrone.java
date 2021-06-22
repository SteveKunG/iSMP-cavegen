package supercoder79.cavegenmod.mixin.structurefix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;

@Mixin(NetherBridgePieces.MonsterThrone.class)
public class MixinNetherBridgePieces_MonsterThrone
{
    @Redirect(method = "postProcess", at = @At(value = "FIELD", target = "net/minecraft/world/level/levelgen/structure/NetherBridgePieces$MonsterThrone.hasPlacedSpawner:Z", ordinal = 0))
    private boolean changeTrue(NetherBridgePieces.MonsterThrone piece)
    {
        return false;
    }
}