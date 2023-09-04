package to_binio.useful_brush.mixin.entity;

import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
