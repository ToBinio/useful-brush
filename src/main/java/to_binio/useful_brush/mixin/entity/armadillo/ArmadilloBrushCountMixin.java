package to_binio.useful_brush.mixin.entity.armadillo;

import net.minecraft.entity.passive.ArmadilloEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import to_binio.useful_brush.BrushCount;

@Mixin (ArmadilloEntity.class)
public class ArmadilloBrushCountMixin implements BrushCount {

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
