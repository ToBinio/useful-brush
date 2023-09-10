package to_binio.useful_brush.mixin.blockEntity;

import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin (CampfireBlockEntity.class)
public interface CampfireBlockEntityAccessor {
    @Accessor ("cookingTimes")
    public int[] getCookingTimes();
}
