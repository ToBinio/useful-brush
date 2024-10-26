package to_binio.useful_brush.entities;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.*;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import to_binio.useful_brush.event.BrushEntityEvent;
import to_binio.useful_brush.mixin.entity.sheep.LootTableDataAccessor;

import static to_binio.useful_brush.UsefulBrush.id;
import static to_binio.useful_brush.entities.BrushableEntities.dropFromLootTable;

public class BrushableEntityEvents {

    public static void register() {
        BrushEntityEvent.getEvent(SheepEntity.class).register((entity, playerEntity, brushLocation) -> {
            SheepEntity sheep = (SheepEntity) entity;
            Random random = sheep.getRandom();
            World world = sheep.getWorld();

            var wool = (Block) LootTableDataAccessor.getWool().get(sheep.getColor());

            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, wool.getDefaultState());

            var sheepHeight = sheep.isBaby() ? 0.3f : 0.8f;
            summonFullSizeAnimalParticles(random, sheep.isBaby(), world, blockStateParticleEffect, brushLocation);

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            dropFromLootTable(world, playerEntity, entity, sheepHeight, id("sheep"));

            return ActionResult.SUCCESS;
        });

        BrushEntityEvent.getEvent(MooshroomEntity.class).register((entity, playerEntity, brushLocation) -> {
            MooshroomEntity mooshroom = (MooshroomEntity) entity;
            Random random = mooshroom.getRandom();
            World world = mooshroom.getWorld();

            var particleBlock = mooshroom.getVariant() == MooshroomEntity.Type.BROWN ? Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState() : Blocks.RED_MUSHROOM_BLOCK.getDefaultState();

            BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, particleBlock);

            var mooshroomHeight = mooshroom.isBaby() ? 0.3f : 0.8f;
            summonFullSizeAnimalParticles(random, mooshroom.isBaby(), world, blockStateParticleEffect, brushLocation);

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            dropFromLootTable(world, playerEntity, entity, mooshroomHeight, id("mooshroom"));

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

    private static void summonFullSizeAnimalParticles(Random random, boolean baby, World world,
            BlockStateParticleEffect blockStateParticleEffect, Vec3d brushLocation) {
        int particleCount = (int) (random.nextBetweenExclusive(7, 12) * (baby ? 0.3 : 1));

        for (int k = 0; k < particleCount; ++k) {
            world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y, brushLocation.z, 3.0 * world.getRandom()
                    .nextDouble() - 1.5, 2.0 * world.getRandom().nextDouble(), 3.0 * world.getRandom()
                    .nextDouble() - 1.5);
        }
    }
}
