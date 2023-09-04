package to_binio.useful_brush.mixin.entiy;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to_binio.useful_brush.BrushCount;
import to_binio.useful_brush.UsefulBrush;

@Mixin (SheepEntity.class)
public class SheepMixin {

    @Unique
    private static final String BrushCountKey = "UsefulBrush.BrushCount";

    @Inject (method = "writeCustomDataToNbt", at = @At (value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        nbt.putInt(BrushCountKey, brushCount.getBrushCount());
    }

    @Inject (method = "readCustomDataFromNbt", at = @At (value = "TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        brushCount.setBrushCount(nbt.getInt(BrushCountKey));
    }

    @Inject (method = "setSheared", at = @At (value = "TAIL"))
    public void setSheared(boolean sheared, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        if (sheared) {
            brushCount.setBrushCount((int) Math.max(brushCount.getBrushCount(), UsefulBrush.SHEEP_MAX_BRUSH_COUNT * 0.5));
        } else {
            brushCount.setBrushCount(0);
        }
    }
}
