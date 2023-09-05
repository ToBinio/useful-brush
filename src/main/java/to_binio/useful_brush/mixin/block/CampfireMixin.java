package to_binio.useful_brush.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushableBlock;

@Mixin (CampfireBlock.class)
public class CampfireMixin implements BrushableBlock {
    @Override
    public boolean brush(PlayerEntity playerEntity, BlockPos blockPos) {

        World world = playerEntity.getWorld();

        BlockState blockState = world.getBlockState(blockPos);

        if (!blockState.get(CampfireBlock.LIT)) {
            return false;
        }

        world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, blockPos, 0);
        CampfireBlock.extinguish(playerEntity, world, blockPos, blockState);
        world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, false), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

        return true;
    }
}