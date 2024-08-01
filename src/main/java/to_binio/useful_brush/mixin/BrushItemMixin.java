package to_binio.useful_brush.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.blocks.BrushableBlocks;
import to_binio.useful_brush.event.BrushBlockEvent;

import static to_binio.useful_brush.entities.BrushableEntities.brush;

@Mixin (BrushItem.class)
public abstract class BrushItemMixin extends ItemMixin {

    @Unique
    final private static double MAX_BRUSH_DISTANCE = Math.sqrt(ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE) - 1.0;

    @Shadow
    protected abstract HitResult getHitResult(LivingEntity user);

    @Inject (at = @At (value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"), method = "usageTick")
    private void usageTickBlock(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci,
            @Local PlayerEntity playerEntity, @Local HitResult hitResult, @Local BlockHitResult blockHitResult,
            @Local BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);

        BrushableBlocks.brush(world, stack, playerEntity, hitResult, blockPos, blockState);
    }

    @Inject (at = @At (shift = At.Shift.AFTER, value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/BrushItem;getHitResult(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/hit/HitResult;"), method = "usageTick", cancellable = true)
    private void usageTickEntity(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
            CallbackInfo ci, @Local (ordinal = 0) PlayerEntity playerEntity, @Local (ordinal = 0) HitResult hitResult) {

        BrushItem brushItem = (BrushItem) (Object) this;

        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {

                int i = brushItem.getMaxUseTime(stack) - remainingUseTicks + 1;
                boolean bl = i % 10 == 5;
                if (bl) {
                    brush(world, stack, playerEntity, entityHitResult);
                }

                ci.cancel();
            }
        }
    }


    @Override
    protected void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
            CallbackInfo ci) {
        BrushCounter.clear(user.getId(), world);
    }

    @Override
    protected void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected,
            CallbackInfo ci) {

        if (entity instanceof LivingEntity player) {
            if (!player.getStackInHand(player.getActiveHand()).isOf(Items.BRUSH)) {
                BrushCounter.clear(player.getId(), world);
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

            if (UsefulBrush.BASIC_BRUSHABLE_BLOCKS.containsKey(blockState.getBlock()) || BrushBlockEvent.hasListener(blockState.getBlock())) {
                return hitResult;
            }

            from = hitResult.getPos();
        }

        return BlockHitResult.createMissed(to, Direction.DOWN, new BlockPos(0, 0, 0));
    }
}
