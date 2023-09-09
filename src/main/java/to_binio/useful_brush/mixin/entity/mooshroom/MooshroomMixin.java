package to_binio.useful_brush.mixin.entity.mooshroom;

import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to_binio.useful_brush.BrushCount;
import to_binio.useful_brush.mixin.entity.LivingEntityMixin;

/**
 * Created: 08/09/2023
 * @author Tobias Frischmann
 */

@Mixin (MooshroomEntity.class)
public abstract class MooshroomMixin extends LivingEntityMixin {

    @Unique
    private static final String BRUSH_COUNT_KEY = "UsefulBrush.BrushCount";
    @Unique
    private static final String BRUSH_COUNT_TIME_KEY = "UsefulBrush.BrushCountTime";

    @Unique
    private int brushCountTime = 0;

    @Inject (method = "writeCustomDataToNbt", at = @At (value = "TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        nbt.putInt(BRUSH_COUNT_KEY, brushCount.getBrushCount());
        nbt.putInt(BRUSH_COUNT_TIME_KEY, brushCountTime);
    }

    @Inject (method = "readCustomDataFromNbt", at = @At (value = "TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        brushCount.setBrushCount(nbt.getInt(BRUSH_COUNT_KEY));
        this.brushCountTime = nbt.getInt(BRUSH_COUNT_TIME_KEY);
    }

    @Override
    protected void tickMovement(CallbackInfo ci) {
        BrushCount brushCount = (BrushCount) this;

        brushCountTime--;

        if (brushCountTime <= 0) {
            brushCount.setBrushCount(Math.max(brushCount.getBrushCount() - 1, 0));
            brushCountTime = Random.create().nextBetween(1000, 2000);
        }
    }
}
