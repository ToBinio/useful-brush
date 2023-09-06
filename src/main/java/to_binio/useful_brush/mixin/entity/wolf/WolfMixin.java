package to_binio.useful_brush.mixin.entity.wolf;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (WolfEntity.class)
public class WolfMixin {
    @Inject (at = @At (value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/passive/WolfEntity;setSitting(Z)V"), method = "interactMob", cancellable = true)
    public void sitting(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.getItem() instanceof BrushItem) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
