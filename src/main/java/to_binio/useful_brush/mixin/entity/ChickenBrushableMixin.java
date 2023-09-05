package to_binio.useful_brush.mixin.entity;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushableEntity;
import to_binio.useful_brush.BrushCount;
import to_binio.useful_brush.UsefulBrush;

@Mixin (ChickenEntity.class)
public class ChickenBrushableMixin implements BrushableEntity {
    @Override
    public boolean brush(PlayerEntity playerEntity) {
        ChickenEntity chicken = (ChickenEntity) (Object) this;
        BrushCount brushCount = chicken;

        if (brushCount.getBrushCount() >= UsefulBrush.CHICKEN_MAX_BRUSH_COUNT || Random.create().nextBetween(0, 5) == 0) {
            return false;
        }

        chicken.dropItem(Items.FEATHER.asItem(), 1);
        chicken.getWorld().playSound(playerEntity, chicken.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        brushCount.setBrushCount(brushCount.getBrushCount() + 1);

        return true;
    }
}
