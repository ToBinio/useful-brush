package to_binio.useful_brush.mixin;

import net.fabricmc.mappings.model.CommentEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import to_binio.useful_brush.UsefulBrush;

import java.util.function.Predicate;

@Mixin (BrushItem.class)
public class BrushItemMixin {

    @Inject (at = @At (value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"), method = "usageTick", locals = LocalCapture.CAPTURE_FAILHARD)
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci,
            PlayerEntity playerEntity, HitResult hitResult, BlockHitResult blockHitResult, int i, boolean bl,
            BlockPos blockPos, BlockState blockState, Arm arm, SoundEvent soundEvent) {
        BlockState block = world.getBlockState(blockPos);

        Block blockToConvert = UsefulBrush.CLEAN_ABLE_BLOCKS.get(block.getBlock());

        if (blockToConvert != null) {

            world.setBlockState(blockPos, blockToConvert.getStateWithProperties(block));

            EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.damage(1, user, (userx) -> {
                userx.sendEquipmentBreakStatus(equipmentSlot);
            });
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
    }
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
