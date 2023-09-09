package to_binio.useful_brush;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import to_binio.useful_brush.event.BrushBlockEvent;

public class BrushableBlocks {

    public static void register() {

        BrushBlockEvent.getEvent(CampfireBlock.class).register((playerEntity, blockPos) -> {
            World world = playerEntity.getWorld();

            BlockState blockState = world.getBlockState(blockPos);

            if (!blockState.get(CampfireBlock.LIT) || world.isClient()) {
                return ActionResult.PASS;
            }

            world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, blockPos, 0);
            CampfireBlock.extinguish(playerEntity, world, blockPos, blockState);
            world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, false), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

            return ActionResult.SUCCESS;
        });

    }
}
