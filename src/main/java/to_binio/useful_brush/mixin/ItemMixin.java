package to_binio.useful_brush.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (Item.class)
public abstract class ItemMixin {

    @Inject (method = "useOnEntity", at = @At (value = "HEAD"), cancellable = true)
    protected void useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
    }

    @Inject (method = "onStoppedUsing", at = @At (value = "HEAD"))
    protected void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
            CallbackInfo ci) {
    }

    @Inject (method = "inventoryTick", at = @At (value = "HEAD"))
    protected void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected,
            CallbackInfo ci) {
    }
}
