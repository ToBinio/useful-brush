package to_binio.useful_brush.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import to_binio.useful_brush.event.BrushEntityEvent;

import static to_binio.useful_brush.blocks.BrushableBlocks.clearBlockBreakingInfo;

public class BrushableEntities {
    public static void brushEntity(World world, LivingEntity user, ItemStack stack, int remainingUseTicks,
            CallbackInfo ci,
            PlayerEntity playerEntity, HitResult hitResult, BrushItem item) {

        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {

                int i = item.getMaxUseTime(stack) - remainingUseTicks + 1;
                boolean bl = i % 10 == 5;
                if (bl) {
                    clearBlockBreakingInfo(world, user);

                    Entity entity = entityHitResult.getEntity();

                    ActionResult brushResult = BrushEntityEvent.getEvent(entity.getClass())
                            .invoker()
                            .brush(entity, playerEntity, hitResult.getPos());

                    if (brushResult == ActionResult.SUCCESS && !world.isClient()) {
                        EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                        stack.damage(1, user, equipmentSlot);
                    }

                    if (brushResult == ActionResult.PASS) {
                        world.playSound(playerEntity, entity.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);
                    }
                }

                ci.cancel();
            }
        }
    }
}
