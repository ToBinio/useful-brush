package to_binio.useful_brush.mixin;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushAble;

@Mixin (SheepEntity.class)
public class SheepMixin implements BrushAble {

    @Override
    public boolean brush(PlayerEntity playerEntity) {
        SheepEntity sheep = (SheepEntity) (Object) this;

        if (sheep.isSheared()) {
            return false;
        }

        sheep.dropItem(Items.STRING.asItem(), 1);

        return true;
    }
}
