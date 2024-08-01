package to_binio.useful_brush.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.event.BrushEntityEvent;

import static to_binio.useful_brush.BrushUtil.handleBrushEvent;

public class BrushableEntities {
    public static void brush(World world, ItemStack stack, PlayerEntity playerEntity, EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();

        var brushableEntityEntry = UsefulBrush.BASIC_BRUSHABLE_ENTITIES.get(entity.getType());
        BrushCounter.brushEntity(entity.getId(), playerEntity.getId(), world);

        entity.getWorld()
                .playSound(playerEntity, entity.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        ActionResult result;

        if (brushableEntityEntry != null) {
            result = brushBrushable(world, playerEntity, stack, entity, brushableEntityEntry, hitResult.getPos());
        } else {
            result = BrushEntityEvent.getEvent(entity.getClass())
                    .invoker()
                    .brush(entity, playerEntity, hitResult.getPos());
        }

        handleBrushEvent(world, stack, playerEntity, result);
    }

    private static ActionResult brushBrushable(World world, PlayerEntity playerEntity, ItemStack stack, Entity entity,
            BrushableEntityEntry brushableEntityEntry, Vec3d brushLocation) {

        Random random = entity.getRandom();

        var isBaby = false;

        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isBaby()) {
                isBaby = true;
            }
        }

        var height = isBaby ? brushableEntityEntry.babyHeight() : brushableEntityEntry.height();
        int particleCount = (int) (random.nextBetweenExclusive(brushableEntityEntry.minParticleCount(), brushableEntityEntry.maxParticleCount()) * (isBaby ? 0.5 : 1));

        for (int k = 0; k < particleCount; ++k) {
            world.addParticle(brushableEntityEntry.particleEffect(), brushLocation.x, brushLocation.y + height, brushLocation.z, world.getRandom()
                    .nextDouble() - 0.5, world.getRandom().nextDouble(), world.getRandom().nextDouble() - .5);
        }

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        dropFromLootTable(world, playerEntity, entity, height, brushableEntityEntry.lootTable());

        return ActionResult.SUCCESS;
    }

    public static void dropFromLootTable(World world, PlayerEntity playerEntity, Entity entity, float height,
            Identifier lootTableId) {
        if (world.getServer() == null) {
            return;
        }

        var lootTable = world.getServer()
                .getReloadableRegistries()
                .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId));

        if (lootTable != LootTable.EMPTY) {
            LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld) world)).add(LootContextParameters.ORIGIN, entity.getPos())
                    .add(LootContextParameters.THIS_ENTITY, entity)
                    .add(LootContextParameters.ATTACKING_ENTITY, playerEntity)
                    .add(LootContextParameters.DAMAGE_SOURCE, world.getDamageSources().generic());

            LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);
            lootTable.generateLoot(lootContextParameterSet, 0L, itemStack -> {
                entity.dropStack(itemStack, height);
            });
        } else {
            UsefulBrush.LOGGER.error("Could not find loot_table '%s'".formatted(lootTableId));
        }
    }
}
