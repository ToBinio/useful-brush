package to_binio.useful_brush.mixin;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import to_binio.useful_brush.BrushAbleEntity;
import to_binio.useful_brush.BrushCount;

@Mixin (SheepEntity.class)
public class SheepBrushCountMixin implements BrushCount {

    @Unique
    public int brushCount = 0;

    @Override
    public int getBrushCount() {
        return brushCount;
    }

    @Override
    public void setBrushCount(int brushCount) {
        this.brushCount = brushCount;
    }
}
