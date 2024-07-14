package to_binio.useful_brush.entities;

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

import static to_binio.useful_brush.entities.BrushableEntities.shouldDrop;

public class BrushableEntityEvents {

    public static void register() {
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
}
