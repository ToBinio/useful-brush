package to_binio.useful_brush.mixin.entity.mooshroom;

import net.minecraft.entity.passive.MooshroomEntity;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushCount;

/**
 * Created: 08/09/2023
 * @author Tobias Frischmann
 */

@Mixin (MooshroomEntity.class)
public class MooshroomBrushCountMixin implements BrushCount {
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
