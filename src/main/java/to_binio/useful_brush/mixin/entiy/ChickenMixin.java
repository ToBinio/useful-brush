package to_binio.useful_brush.mixin.entiy;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushAbleEntity;

@Mixin (ChickenEntity.class)
public class ChickenMixin implements BrushAbleEntity {
    @Override
    public boolean brush(PlayerEntity playerEntity) {
        ChickenEntity chicken = (ChickenEntity) (Object) this;

        chicken.dropItem(Items.FEATHER.asItem(), 1);
        chicken.getWorld().playSound(playerEntity, chicken.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        return true;
    }
}
