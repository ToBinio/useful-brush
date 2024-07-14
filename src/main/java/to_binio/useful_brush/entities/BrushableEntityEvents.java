package to_binio.useful_brush.entities;

import net.fabricmc.loader.impl.lib.tinyremapper.VisitTrackingClassRemapper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import to_binio.useful_brush.BrushCounter;
import to_binio.useful_brush.config.UsefulBrushConfig;
import to_binio.useful_brush.event.BrushEntityEvent;
import to_binio.useful_brush.mixin.entity.sheep.SheepAccessor;

public class BrushableEntityEvents {

    public static void register() {
        BrushEntityEvent.getEvent(ChickenEntity.class).register((entity, playerEntity, brushLocation) -> {
            ChickenEntity chicken = (ChickenEntity) entity;
            Random random = chicken.getRandom();
            World world = chicken.getWorld();

            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CALCITE.getDefaultState());

            var chickenHeight = chicken.isBaby() ? 0.1 : 0.3;
            int particleCount = (int) (random.nextBetweenExclusive(2, 5) * (chicken.isBaby() ? 0.5 : 1));

            for (int k = 0; k < particleCount; ++k) {
                world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + chickenHeight, brushLocation.z, world.getRandom()
                        .nextDouble() - 0.5, world.getRandom().nextDouble(), world.getRandom().nextDouble() - .5);
            }

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            if (!shouldDrop(random, BrushCounter.get(playerEntity.getId(), world.isClient()), (int) (UsefulBrushConfig.INSTANCE.CHICKEN_DROP_COUNT * (chicken.isBaby() ? 2 : 1)))) {
                return ActionResult.PASS;
            }

            chicken.dropStack(new ItemStack(Items.FEATHER.asItem()), (float) chickenHeight);
            chicken.getWorld()
                    .playSound(playerEntity, chicken.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

            return ActionResult.SUCCESS;
        });

        BrushEntityEvent.getEvent(MooshroomEntity.class).register((entity, playerEntity, brushLocation) -> {
            MooshroomEntity mooshroom = (MooshroomEntity) entity;
            Random random = mooshroom.getRandom();
            World world = mooshroom.getWorld();

            var mooshroomHeight = mooshroom.isBaby() ? 0.3 : 0.8;

            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.RED_MUSHROOM_BLOCK.getDefaultState());

            int particleCount = (int) (random.nextBetweenExclusive(7, 12) * (mooshroom.isBaby() ? 0.5 : 1));

            for (int k = 0; k < particleCount; ++k) {
                world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + mooshroomHeight, brushLocation.z, 3.0 * world.getRandom()
                        .nextDouble() - 1.5, 2.0 * world.getRandom().nextDouble(), 3.0 * world.getRandom()
                        .nextDouble() - 1.5);
            }

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            if (!shouldDrop(random, BrushCounter.get(playerEntity.getId(), world.isClient()), (int) (UsefulBrushConfig.INSTANCE.MOOSHROOM_DROP_COUNT * (mooshroom.isBaby() ? 2 : 1)))) {
                return ActionResult.PASS;
            }

            if (random.nextBetween(0, 5) == 0) {
                mooshroom.dropStack(new ItemStack(Items.BROWN_MUSHROOM.asItem()), (float) mooshroomHeight);
            } else {
                mooshroom.dropStack(new ItemStack(Items.RED_MUSHROOM.asItem()), (float) mooshroomHeight);
            }

            world.playSound(playerEntity, mooshroom.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

            return ActionResult.SUCCESS;
        });

        BrushEntityEvent.getEvent(ArmadilloEntity.class).register((entity, playerEntity, brushLocation) -> {
            ArmadilloEntity armadillo = (ArmadilloEntity) entity;
            Random random = armadillo.getRandom();
            World world = armadillo.getWorld();

            var height = armadillo.isBaby() ? 0.2 : 0.4;

            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SAND.getDefaultState());

            int particleCount = (int) (random.nextBetweenExclusive(4, 6) * (armadillo.isBaby() ? 0.5 : 1));

            for (int k = 0; k < particleCount; ++k) {
                world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + height, brushLocation.z, 3.0 * world.getRandom()
                        .nextDouble() - 1.5, 2.0 * world.getRandom().nextDouble(), 3.0 * world.getRandom()
                        .nextDouble() - 1.5);
            }

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            if (!shouldDrop(random, BrushCounter.get(playerEntity.getId(), world.isClient()), UsefulBrushConfig.INSTANCE.ARMADILLO_DROP_COUNT * (armadillo.isBaby() ? 2 : 1))) {
                return ActionResult.PASS;
            }

            armadillo.dropStack(new ItemStack(Items.ARMADILLO_SCUTE.asItem()), (float) height);

            world.playSound(playerEntity, armadillo.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

            return ActionResult.SUCCESS;
        });

        BrushEntityEvent.getEvent(SheepEntity.class).register((entity, playerEntity, brushLocation) -> {
            SheepEntity sheep = (SheepEntity) entity;
            Random random = sheep.getRandom();
            World world = sheep.getWorld();

            var wool = (Block) SheepAccessor.getDrops().get(sheep.getColor());

            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, wool.getDefaultState());

            var sheepHeight = sheep.isBaby() ? 0.3 : 0.8;
            int particleCount = (int) (random.nextBetweenExclusive(7, 12) * (sheep.isBaby() ? 0.3 : 1));

            for (int k = 0; k < particleCount; ++k) {
                world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + sheepHeight, brushLocation.z, 3.0 * world.getRandom()
                        .nextDouble() - 1.5, 2.0 * world.getRandom().nextDouble(), 3.0 * world.getRandom()
                        .nextDouble() - 1.5);
            }

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            if (!shouldDrop(random, BrushCounter.get(playerEntity.getId(), world.isClient()), UsefulBrushConfig.INSTANCE.SHEEP_DROP_COUNT * (sheep.isBaby() ? 2 : 1))) {
                return ActionResult.PASS;
            }

            sheep.dropStack(new ItemStack(Items.STRING.asItem()), (float) sheepHeight);
            world.playSound(playerEntity, sheep.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

            return ActionResult.SUCCESS;
        });

        BrushEntityEvent.getEvent(WolfEntity.class).register((entity, playerEntity, brushLocation) -> {

            WolfEntity wolf = (WolfEntity) entity;

            if (!wolf.isOwner(playerEntity)) {
                return ActionResult.PASS;
            }

            double dogSize;
            double dogHeight;

            if (wolf.isBaby()) {
                dogSize = 0.3;
                dogHeight = 0.8;
            } else {
                dogSize = 0.4;
                dogHeight = 1.2;
            }

            wolf.heal(1);

            ClientWorld world = MinecraftClient.getInstance().world;

            double angle = Math.toRadians(wolf.getBodyYaw() + 90);

            double xOffset = Math.cos(angle) * dogSize;
            double zOffset = Math.sin(angle) * dogSize;

            world.addParticle(ParticleTypes.HEART, wolf.getX() + xOffset, wolf.getY() + dogHeight, wolf.getZ() + zOffset, 0, 4, 0);

            return ActionResult.SUCCESS;
        });
    }

    public static boolean shouldDrop(Random random, int brushCount, int goalAmount) {
        return random.nextBetween(0, Math.max(goalAmount - brushCount, 0)) == 0;
    }
}
