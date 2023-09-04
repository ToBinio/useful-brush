package to_binio.useful_brush.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (Item.class)
public class ItemMixin {
    @Inject (at = @At (value = "HEAD"), method = "useOnEntity", cancellable = true)
    private void useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {

        if (stack.getItem() instanceof BrushItem brush) {

            BrushItemInvoker brushItemInvoker = (BrushItemInvoker) brush;

            if (user != null && brushItemInvoker.invokeGetHitResult(user).getType() == HitResult.Type.ENTITY) {
                user.setCurrentHand(hand);
            }

            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
