package to_binio.useful_brush;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import to_binio.useful_brush.event.BrushBlockEvent;
import to_binio.useful_brush.mixin.blockEntity.CampfireBlockEntityAccessor;

public class BrushableBlocks {

    public static void register() {

        BrushBlockEvent.getEvent(CampfireBlock.class).register((playerEntity, blockPos) -> {
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

    }
}
