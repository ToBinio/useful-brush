package to_binio.useful_brush.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.event.BrushEntityEvent;

import static to_binio.useful_brush.BrushUtil.handleBrushEvent;

public class BrushableEntities {
    public static void brushEntity(World world, ItemStack stack, PlayerEntity playerEntity, EntityHitResult hitResult) {

        Entity entity = hitResult.getEntity();
        BrushCounter.brushEntity(entity.getId(), playerEntity.getId(), world);

        ActionResult brushResult = BrushEntityEvent.getEvent(entity.getClass())
                .invoker()
                .brush(entity, playerEntity, hitResult.getPos());

        handleBrushEvent(world, stack, playerEntity, brushResult);

        if (brushResult == ActionResult.PASS) {
            world.playSound(playerEntity, entity.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);
        }
    }
}
