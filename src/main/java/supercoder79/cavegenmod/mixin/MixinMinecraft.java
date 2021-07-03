package supercoder79.cavegenmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    @ModifyVariable(method = "doLoadLevel(Ljava/lang/String;Lnet/minecraft/core/RegistryAccess$RegistryHolder;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/Minecraft$ExperimentalDialogType;)V", at = @At(value = "FIELD", target = "net/minecraft/client/Minecraft$ExperimentalDialogType.NONE:Lnet/minecraft/client/Minecraft$ExperimentalDialogType;", shift = At.Shift.BEFORE), index = 11, ordinal = 1)
    private boolean disableIsLegacyWorldType(boolean dValue)
    {
        return false;
    }

    @ModifyVariable(method = "doLoadLevel(Ljava/lang/String;Lnet/minecraft/core/RegistryAccess$RegistryHolder;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/Minecraft$ExperimentalDialogType;)V", at = @At(value = "FIELD", target = "net/minecraft/client/Minecraft$ExperimentalDialogType.NONE:Lnet/minecraft/client/Minecraft$ExperimentalDialogType;", shift = At.Shift.BEFORE), index = 11, ordinal = 2)
    private boolean disableLifeCycleNotStable(boolean dValue)
    {
        return false;
    }
}