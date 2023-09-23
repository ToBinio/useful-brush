package to_binio.useful_brush.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import to_binio.useful_brush.event.BrushEntityEvent;

@Mixin (BrushItem.class)
public abstract class BrushItemMixin extends ItemMixin {

    @Shadow
    @Final
    private static double MAX_BRUSH_DISTANCE;

    @Shadow
    protected abstract HitResult getHitResult(LivingEntity user);

    @Inject (at = @At (value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"), method = "usageTick")
    private void usageTickBlock(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci,
            @Local PlayerEntity playerEntity, @Local HitResult hitResult, @Local BlockHitResult blockHitResult,
            @Local BlockPos blockPos) {
        BlockState blockState = world.getBlockState(blockPos);

        var blockEntry = UsefulBrush.BRUSHABLE_BLOCKS.get(blockState.getBlock());

        if (blockEntry != null) {
            if (!world.isClient()) {
                world.setBlockState(blockPos, blockEntry.block().getStateWithProperties(blockState));

                EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.damage(1, user, (userx) -> {
                    userx.sendEquipmentBreakStatus(equipmentSlot);
                });

                if (blockEntry.lootTable() != null) {
                    var lootTable = world.getServer().getLootManager().getLootTable(blockEntry.lootTable());

                    if (lootTable != LootTable.EMPTY) {
                        LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld) world)).add(LootContextParameters.ORIGIN, blockPos.toCenterPos()).add(LootContextParameters.TOOL, ItemStack.EMPTY).add(LootContextParameters.BLOCK_STATE, blockState);

                        LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.BLOCK);
                        lootTable.generateLoot(lootContextParameterSet, 0L, itemStack -> {

                            UsefulBrush.LOGGER.info(itemStack.getItem().getName().toString());

                            Vec3d pos = hitResult.getPos();
                            Vec3d center = blockPos.toCenterPos();

                            Vec3d offset = pos.subtract(center);

                            Vec3d spawnPos = pos.add(offset.normalize().multiply(0.2));

                            ItemEntity itemEntity = new ItemEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), itemStack);
                            itemEntity.setToDefaultPickupDelay();
                            world.spawnEntity(itemEntity);
                        });
                    } else {
                        UsefulBrush.LOGGER.error("Could not find loot_table '%s'".formatted(blockEntry.lootTable()));
                    }
                }
            }
        } else {
            ActionResult brushResult = BrushBlockEvent.getEvent(blockState.getBlock()).invoker().brush(playerEntity, blockPos);

            if (brushResult == ActionResult.SUCCESS && !world.isClient()) {
                EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.damage(1, user, ((userx) -> {
                    userx.sendEquipmentBreakStatus(equipmentSlot);
                }));
            }
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

                    ActionResult brushResult = BrushEntityEvent.getEvent(entity.getClass()).invoker().brush(entity, playerEntity, hitResult.getPos());

                    if (brushResult == ActionResult.SUCCESS && !world.isClient()) {
                        EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                        stack.damage(1, user, ((userx) -> {
                            userx.sendEquipmentBreakStatus(equipmentSlot);
                        }));
                    }

                    if (brushResult == ActionResult.PASS) {
                        world.playSound(playerEntity, entity.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);
                    }
                }

                ci.cancel();
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

            HitResult enityHitResult = ProjectileUtil.getEntityCollision(world, user, from, to, user.getBoundingBox().stretch(velocity).expand(1.0), (entity) -> !entity.isSpectator() && entity.canHit());

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

            if (UsefulBrush.BRUSHABLE_OUTLINE_BLOCKS.contains(blockState.getBlock())) {
                return hitResult;
            }

            from = hitResult.getPos();
        }

        return BlockHitResult.createMissed(to, Direction.DOWN, new BlockPos(0, 0, 0));
    }
}
