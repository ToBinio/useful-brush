package to_binio.useful_brush.mixin.blockEntity;

import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to_binio.useful_brush.BrushUtil;

/**
 * Created: 16.05.25
 *
 * @author Tobias Frischmann
 */
@Mixin (BrushableBlockEntity.class)
public class BrushableBlockEntityMixin {
    @Shadow private long nextBrushTime;

    @Inject (method = "brush", at = @At (value = "INVOKE", target = "Lnet/minecraft/block/entity/BrushableBlockEntity;generateItem(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void useMiningEfficiency(long worldTime, ServerWorld world, LivingEntity brusher, Direction hitDirection,
            ItemStack brush, CallbackInfoReturnable<Boolean> cir){
        this.nextBrushTime = worldTime + Math.round(10 * BrushUtil.getBrushEfficiency(brusher));
    }
}
