package to_binio.useful_brush.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin (BrushItem.class)
public interface BrushItemInvoker {
    @Invoker ("getHitResult")
    public HitResult invokeGetHitResult(LivingEntity user);
}
