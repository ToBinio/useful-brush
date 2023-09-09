package to_binio.useful_brush.mixin.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to_binio.useful_brush.BrushCount;

@Mixin (LivingEntity.class)
public class LivingEntityMixin {
    @Inject (method = "tickMovement", at = @At (value = "HEAD"))
    protected void tickMovement(CallbackInfo ci) {
    }
}
