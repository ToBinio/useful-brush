package to_binio.useful_brush.mixin.entity.mooshroom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to_binio.useful_brush.BrushCount;
import to_binio.useful_brush.UsefulBrush;

/**
 * Created: 08/09/2023
 * @author Tobias Frischmann
 */

@Mixin (MooshroomEntity.class)
public class MooshroomMixin extends CowEntity {
    @Nullable
    @Override
    public MooshroomEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        MooshroomEntity mooshroomEntity = (MooshroomEntity) EntityType.MOOSHROOM.create(serverWorld);
        if (mooshroomEntity != null) {

            MooshroomAccessor mooshroomAccessor = (MooshroomAccessor) mooshroomEntity;

            mooshroomEntity.setVariant(mooshroomAccessor.invokerChooseBabyType((MooshroomEntity) passiveEntity));
        }

        return mooshroomEntity;
    }

    @Unique
    private static final String BRUSH_COUNT_KEY = "UsefulBrush.BrushCount";
    @Unique
    private static final String BRUSH_COUNT_TIME_KEY = "UsefulBrush.BrushCountTime";

    @Unique
    private int brushCountTime = 0;

    public MooshroomMixin(EntityType<? extends MooshroomEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject (method = "writeCustomDataToNbt", at = @At (value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        nbt.putInt(BRUSH_COUNT_KEY, brushCount.getBrushCount());
        nbt.putInt(BRUSH_COUNT_TIME_KEY, brushCountTime);
    }

    @Inject (method = "readCustomDataFromNbt", at = @At (value = "TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {

        BrushCount brushCount = (BrushCount) this;

        brushCount.setBrushCount(nbt.getInt(BRUSH_COUNT_KEY));
        this.brushCountTime = nbt.getInt(BRUSH_COUNT_TIME_KEY);
    }


    @Override
    public void tickMovement() {
        super.tickMovement();

        BrushCount brushCount = (BrushCount) this;

        brushCountTime--;

        if (brushCountTime <= 0) {
            brushCount.setBrushCount(Math.max(brushCount.getBrushCount() - 1, 0));
            brushCountTime = Random.create().nextBetween(1000, 2000);
        }
    }
}
