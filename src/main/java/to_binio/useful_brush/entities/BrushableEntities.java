package to_binio.useful_brush.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.config.UsefulBrushConfig;
import to_binio.useful_brush.event.BrushEntityEvent;

import static to_binio.useful_brush.BrushUtil.handleBrushEvent;

public class BrushableEntities {
    public static void brush(World world, ItemStack stack, PlayerEntity playerEntity, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();

        var brushableEntityEntry = UsefulBrush.BASIC_BRUSHABLE_ENTITIES.get(entity.getType());
        BrushCounter.brushEntity(entity.getId(), playerEntity.getId(), world);

        ActionResult result;

        if (brushableEntityEntry != null) {
            result = brushBrushable(world, playerEntity, entity, brushableEntityEntry, hitResult.getPos());
        } else {
            result = BrushEntityEvent.getEvent(entity.getClass())
                    .invoker()
                    .brush(entity, playerEntity, hitResult.getPos());
        }

        handleBrushEvent(world, stack, playerEntity, result);

        if (result == ActionResult.PASS) {
            world.playSound(playerEntity, entity.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);
        }
    }

    private static ActionResult brushBrushable(World world, PlayerEntity playerEntity, Entity entity,
            BrushableEntityEntry brushableEntityEntry, Vec3d brushLocation) {

        Random random = entity.getRandom();

        var isBaby = false;

        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isBaby()) {
                isBaby = true;
            }
        }

        var height = isBaby ? brushableEntityEntry.babyHeight() : brushableEntityEntry.height();
        int particleCount = (int) (random.nextBetweenExclusive(brushableEntityEntry.minParticleCount(), brushableEntityEntry.maxParticleCount())
                * (isBaby ? 0.5 : 1));

        for (int k = 0; k < particleCount; ++k) {
            world.addParticle(brushableEntityEntry.particleEffect(), brushLocation.x, brushLocation.y + height, brushLocation.z, world.getRandom()
                    .nextDouble() - 0.5, world.getRandom().nextDouble(), world.getRandom().nextDouble() - .5);
        }

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!shouldDrop(random, BrushCounter.get(playerEntity.getId(), world.isClient()), (int) (UsefulBrushConfig.INSTANCE.CHICKEN_DROP_COUNT * (isBaby ? 2 : 1)))) {
            return ActionResult.PASS;
        }

        entity.dropStack(new ItemStack(brushableEntityEntry.drop().apply(entity).asItem()), height);
        entity.getWorld()
                .playSound(playerEntity, entity.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        return ActionResult.SUCCESS;
    }

    public static boolean shouldDrop(Random random, int brushCount, int goalAmount) {
        return random.nextBetween(0, Math.max(goalAmount - brushCount, 0) + 1) == 0;
    }
}
