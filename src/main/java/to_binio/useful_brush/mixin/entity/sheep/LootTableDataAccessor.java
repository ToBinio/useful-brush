package to_binio.useful_brush.mixin.entity.sheep;

import net.minecraft.data.server.loottable.LootTableData;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin (LootTableData.class)
public interface LootTableDataAccessor {
    @Accessor ("WOOL_FROM_DYE_COLOR")
    static Map<DyeColor, ItemConvertible> getWool() {
        throw new AssertionError();
    }
}
