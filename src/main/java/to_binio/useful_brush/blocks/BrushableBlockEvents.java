package to_binio.useful_brush.blocks;

import net.minecraft.block.*;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import to_binio.useful_brush.event.BrushBlockEvent;
import to_binio.useful_brush.mixin.blockEntity.CampfireBlockEntityAccessor;

public class BrushableBlockEvents {

    public static void register() {

        BrushBlockEvent.getEvent(Blocks.CAMPFIRE).register((playerEntity, blockPos) -> {
            World world = playerEntity.getWorld();
            BlockState blockState = world.getBlockState(blockPos);
            CampfireBlockEntityAccessor campfireEntity = (CampfireBlockEntityAccessor) world.getBlockEntity(blockPos);

            if (!blockState.get(CampfireBlock.LIT)) {
                return ActionResult.PASS;
            }

            int[] cookingTimes = campfireEntity.getCookingTimes();

            for (int i = 0; i < cookingTimes.length; i++) {
                cookingTimes[i] = (cookingTimes[i] + 10);
            }

            for (int i = 0; i < 6; ++i) {
                CampfireBlock.spawnSmokeParticle(world, blockPos, false, true);
            }

            return ActionResult.SUCCESS;
        });

        BrushBlockEvent.getEvent(Blocks.SNOW).register((playerEntity, blockPos) -> {
            World world = playerEntity.getWorld();

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            BlockState blockState = world.getBlockState(blockPos);

            int newHeight = blockState.get(SnowBlock.LAYERS) - 1;

            if (newHeight >= 1) {
                world.setBlockState(blockPos, blockState.with(SnowBlock.LAYERS, newHeight));
            } else {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
            }


            return ActionResult.SUCCESS;
        });

        BrushBlockEvent.getEvent(Blocks.SNOW_BLOCK).register((playerEntity, blockPos) -> {
            World world = playerEntity.getWorld();

            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            world.setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7));

            return ActionResult.SUCCESS;
        });
    }
}
