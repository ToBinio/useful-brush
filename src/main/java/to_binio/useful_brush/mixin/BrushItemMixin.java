package to_binio.useful_brush.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import to_binio.useful_brush.BrushAble;
import to_binio.useful_brush.UsefulBrush;

@Mixin (BrushItem.class)
public class BrushItemMixin {

    @Inject (at = @At (value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"), method = "usageTick")
    private void usageTickBlock(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci,
            @Local PlayerEntity playerEntity, @Local HitResult hitResult, @Local BlockHitResult blockHitResult,
            @Local BlockPos blockPos) {
        BlockState block = world.getBlockState(blockPos);

        Block blockToConvert = UsefulBrush.CLEAN_ABLE_BLOCKS.get(block.getBlock());

        if (blockToConvert != null) {

            world.setBlockState(blockPos, blockToConvert.getStateWithProperties(block));

            EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.damage(1, user, (userx) -> {
                userx.sendEquipmentBreakStatus(equipmentSlot);
            });
        }
    }


    @Inject (at = @At (shift = At.Shift.AFTER, value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/BrushItem;getHitResult(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/hit/HitResult;"), method = "usageTick", cancellable = true)
    private void usageTickEntity(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
            CallbackInfo ci,
            @Local (ordinal = 0) PlayerEntity playerEntity, @Local (ordinal = 0) HitResult hitResult) {

        BrushItem item = (BrushItem) (Object) this;

        if (hitResult instanceof EntityHitResult entityHitResult) {

            if (hitResult.getType() == HitResult.Type.ENTITY) {

                int i = item.getMaxUseTime(stack) - remainingUseTicks + 1;
                boolean bl = i % 10 == 5;
                if (bl) {

                    Entity entity = entityHitResult.getEntity();

                    SoundEvent soundEvent = SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC;

                    world.playSound(playerEntity, entity.getBlockPos(), soundEvent, SoundCategory.BLOCKS);
                    if (!world.isClient()) {
                        if (entity instanceof BrushAble brushAble) {
                            boolean bl2 = brushAble.brush();
                            if (bl2) {
                                EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                                stack.damage(1, user, ((userx) -> {
                                    userx.sendEquipmentBreakStatus(equipmentSlot);
                                }));
                            }
                        }
                    }
                }

                ci.cancel();
            }
        }
    }


//        BlockState blockStateToConvert = UsefulBrush.CLEAN_ABLE_BLOCK_STATES.get(block);
//
//        if (blockStateToConvert != null) {
//
//            world.setBlockState(blockPos, blockStateToConvert);
//
//            EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
//            stack.damage(1, user, (userx) -> {
//                userx.sendEquipmentBreakStatus(equipmentSlot);
//            });
//        }
//}
//
//    @Redirect (method = "getHitResult", at = @At (value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;getCollision(Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/HitResult;"))
//    public HitResult getHitResult(Entity entity, Predicate<Entity> predicate, double range) {
//        return hit(entity, range);
//    }
//
//    @Unique
//    private static HitResult hit(Entity user, double range) {
//
//        Vec3d velocity = user.getRotationVec(0.0F).multiply(range);
//        World world = user.getWorld();
//        Vec3d pos = user.getEyePos();
//
//        Vec3d vec3d = pos.add(velocity);
//
//        return world.raycast(new RaycastContext(pos, vec3d, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, user));
//    }
}
