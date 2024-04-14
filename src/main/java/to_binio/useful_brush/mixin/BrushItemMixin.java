package to_binio.useful_brush.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.event.BrushBlockEvent;

import static to_binio.useful_brush.blocks.BrushableBlocks.*;
import static to_binio.useful_brush.entities.BrushableEntities.brushEntity;

@Mixin (BrushItem.class)
public abstract class BrushItemMixin extends ItemMixin {

    @Unique
    @Final
    private static double MAX_BRUSH_DISTANCE;

    @Shadow
    protected abstract HitResult getHitResult(LivingEntity user);

    @Inject (at = @At (value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"), method = "usageTick")
    private void usageTickBlock(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci,
            @Local PlayerEntity playerEntity, @Local HitResult hitResult, @Local BlockHitResult blockHitResult,
            @Local BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);

        brushBlock(world, stack, playerEntity, hitResult, blockPos, blockState);
    }

    @Inject (at = @At (shift = At.Shift.AFTER, value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/BrushItem;getHitResult(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/hit/HitResult;"), method = "usageTick", cancellable = true)
    private void usageTickEntity(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
            CallbackInfo ci, @Local (ordinal = 0) PlayerEntity playerEntity, @Local (ordinal = 0) HitResult hitResult) {

        brushEntity(world, user, stack, remainingUseTicks, ci, playerEntity, hitResult, (BrushItem) (Object) this);
    }


    @Override
    protected void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
            CallbackInfo ci) {
        clearBlockBreakingInfo(world, user);
    }

    @Override
    protected void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected,
            CallbackInfo ci) {

        if (!selected) {
            if (entity instanceof LivingEntity livingEntity) {
                clearBlockBreakingInfo(world, livingEntity);
            }
        }
    }


    @Override
    protected void useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {

        if (user != null && this.getHitResult(user).getType() == HitResult.Type.ENTITY) {
            user.setCurrentHand(hand);
        }

        cir.setReturnValue(ActionResult.CONSUME);
    }

    @Inject (method = "getHitResult", at = @At (value = "HEAD"), cancellable = true)
    private void myGetHitResult(LivingEntity user, CallbackInfoReturnable<HitResult> cir) {
        HitResult hitResult = getSpecialBlockHitResult(user);
        if (hitResult.getType() != HitResult.Type.MISS) {
            Vec3d velocity = user.getRotationVec(0.0F).multiply(MAX_BRUSH_DISTANCE);
            World world = user.getWorld();
            Vec3d from = user.getEyePos();
            Vec3d to = hitResult.getPos();

            HitResult enityHitResult = ProjectileUtil.getEntityCollision(world, user, from, to, user.getBoundingBox()
                    .stretch(velocity)
                    .expand(1.0), (entity) -> !entity.isSpectator() && entity.canHit());

            if (enityHitResult == null) {
                cir.setReturnValue(hitResult);
            }
        }
    }

    @Unique
    private static BlockHitResult getSpecialBlockHitResult(LivingEntity user) {
        Vec3d velocity = user.getRotationVec(0.0F).multiply(MAX_BRUSH_DISTANCE);
        World world = user.getWorld();
        Vec3d from = user.getEyePos();
        Vec3d to = from.add(velocity);

        while (!from.isInRange(to, 1)) {
            BlockHitResult hitResult = world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, user));
            if (hitResult.getType() == HitResult.Type.MISS) break;

            BlockState blockState = user.getWorld().getBlockState(hitResult.getBlockPos());

            if (UsefulBrush.BRUSHABLE_BLOCKS.containsKey(blockState.getBlock()) || BrushBlockEvent.hasListener(blockState.getBlock())) {
                return hitResult;
            }

            from = hitResult.getPos();
        }

        return BlockHitResult.createMissed(to, Direction.DOWN, new BlockPos(0, 0, 0));
    }
}
