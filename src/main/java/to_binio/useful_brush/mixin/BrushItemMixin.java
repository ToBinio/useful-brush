package to_binio.useful_brush.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.BrushUtil;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.blocks.BrushableBlocks;
import to_binio.useful_brush.event.BrushBlockEvent;

import static to_binio.useful_brush.entities.BrushableEntities.brush;

@Mixin (BrushItem.class)
public abstract class BrushItemMixin extends ItemMixin {

    @Shadow
    protected abstract HitResult getHitResult(PlayerEntity user);

    @Shadow
    public abstract int getMaxUseTime(ItemStack stack, LivingEntity user);

    @Inject (at = @At (value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"), method = "usageTick", cancellable = true)
    private void usageVisualTickBlock(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci, @Local BlockHitResult blockHitResult, @Local BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);
        BrushableBlocks.brush(world, stack, (PlayerEntity) user, blockHitResult, blockPos, blockState, true);

        //cancel vanilla handling of brushableBlockEntity's since this is done in usageTickBlock()
        ci.cancel();
    }

    @Inject (method = "usageTick", at = @At (value = "INVOKE", target = "Lnet/minecraft/item/BrushItem;getMaxUseTime(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)I"))
    private void usageTickBlock(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
            CallbackInfo ci, @Local BlockHitResult blockHitResult) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks + 1;

        if (shouldTickBrush(user, i)) {
            BlockPos blockPos = blockHitResult.getBlockPos();

            if (world instanceof ServerWorld serverWorld && world.getBlockEntity(blockPos) instanceof BrushableBlockEntity brushableBlockEntity) {
                boolean bl2 = brushableBlockEntity.brush(world.getTime(), serverWorld, user, blockHitResult.getSide(), stack);
                if (bl2) {
                    EquipmentSlot equipmentSlot = stack.equals(user.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    stack.damage(1, user, equipmentSlot);
                }
            }else{
                BlockState blockState = world.getBlockState(blockPos);
                BrushableBlocks.brush(world, stack, (PlayerEntity) user, blockHitResult, blockPos, blockState, false);
            }
        }
    }


    @Inject (at = @At (shift = At.Shift.AFTER, value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/BrushItem;getHitResult(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/util/hit/HitResult;"), method = "usageTick", cancellable = true)
    private void usageTickEntity(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
            CallbackInfo ci, @Local (ordinal = 0) PlayerEntity playerEntity, @Local (ordinal = 0) HitResult hitResult) {

        BrushItem brushItem = (BrushItem) (Object) this;

        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                int i = brushItem.getMaxUseTime(stack, playerEntity) - remainingUseTicks + 1;

                if (shouldTickBrush(playerEntity, i)) {
                    brush(world, stack, playerEntity, entityHitResult, false);
                }

                if (i % 10 == 5) {
                    brush(world, stack, playerEntity, entityHitResult, true);
                }

                ci.cancel();
            }
        }
    }


    @Override
    protected void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
            CallbackInfoReturnable<Boolean> ci) {
        BrushCounter.clear(user.getId(), world);
    }

    @Override
    protected void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot,
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
    private void myGetHitResult(PlayerEntity user, CallbackInfoReturnable<HitResult> cir) {
        HitResult hitResult = getSpecialBlockHitResult(user);
        if (hitResult.getType() != HitResult.Type.MISS) {
            Vec3d velocity = user.getRotationVec(0.0F).multiply(user.getBlockInteractionRange());
            World world = user.getWorld();
            Vec3d from = user.getEyePos();
            Vec3d to = hitResult.getPos();

            HitResult enityHitResult = ProjectileUtil.getEntityCollision(world,
                    user,
                    from,
                    to,
                    user.getBoundingBox().stretch(velocity).expand(1.0),
                    (entity) -> !entity.isSpectator() && entity.canHit());

            if (enityHitResult == null) {
                cir.setReturnValue(hitResult);
            }
        }
    }

    @Unique
    private static BlockHitResult getSpecialBlockHitResult(PlayerEntity user) {
        Vec3d velocity = user.getRotationVec(0.0F).multiply(user.getBlockInteractionRange());
        World world = user.getWorld();
        Vec3d from = user.getEyePos();
        Vec3d to = from.add(velocity);

        while (!from.isInRange(to, 1)) {
            BlockHitResult hitResult = world.raycast(new RaycastContext(from,
                    to,
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    user));
            if (hitResult.getType() == HitResult.Type.MISS) break;

            BlockState blockState = user.getWorld().getBlockState(hitResult.getBlockPos());

            if (blockState.isFullCube(world, hitResult.getBlockPos()) || UsefulBrush.BASIC_BRUSHABLE_BLOCKS.containsKey(
                    blockState.getBlock()) || BrushBlockEvent.hasListener(blockState.getBlock())) {
                return hitResult;
            }

            from = hitResult.getPos();
        }

        return BlockHitResult.createMissed(to, Direction.DOWN, new BlockPos(0, 0, 0));
    }

    @Unique
    private boolean shouldTickBrush(LivingEntity user, int tick) {
        double tickPerAction = Math.round(10 * BrushUtil.getBrushEfficiency(user));
        return tick % tickPerAction == tickPerAction / 2;
    }
}
