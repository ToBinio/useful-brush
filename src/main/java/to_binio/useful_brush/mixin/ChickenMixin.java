package to_binio.useful_brush.mixin;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushAble;

@Mixin (ChickenEntity.class)
public class ChickenMixin implements BrushAble {
    @Override
    public boolean brush(PlayerEntity playerEntity) {
        ChickenEntity chicken = (ChickenEntity) (Object) this;

        chicken.dropItem(Items.FEATHER.asItem(), 1);

        return true;
    }
}
