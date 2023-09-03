package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.projectile.ProjectileUtil;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;

import java.util.HashMap;
import java.util.Map;

public class UsefulBrush implements ModInitializer {

    public static final HashMap<Block, Block> CLEAN_ABLE_BLOCKS = new HashMap<>();
    public static final HashMap<BlockState, BlockState> CLEAN_ABLE_BLOCK_STATES = new HashMap<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE);
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS);
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB);
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL);

        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS);
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS);
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
        CLEAN_ABLE_BLOCKS.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.STONE_BRICK_WALL);

        CLEAN_ABLE_BLOCKS.put(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);

        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState(), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 8), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 6));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 6), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 5));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 5), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 4));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 4), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 3));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 3), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 2));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 2), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1));
        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1), Blocks.AIR.getDefaultState());

    }
}
