package to_binio.useful_brush.mixin.entity.armadillo;

import net.minecraft.entity.passive.ArmadilloEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (ArmadilloEntity.class)
public class ArmadilloMixin {
    @Inject (method = "brushScute", at = @At (value = "HEAD"), cancellable = true)
    private void brushScute(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
