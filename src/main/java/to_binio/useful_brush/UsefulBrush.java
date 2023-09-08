package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class UsefulBrush implements ModInitializer {


    public static final String MOD_ID = "useful_brush";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int SHEEP_MAX_BRUSH_COUNT = 3;
    public static final int CHICKEN_MAX_BRUSH_COUNT = 2;
    public static final int MOOSHROOM_MAX_BRUSH_COUNT = 1;

    public static final HashMap<Block, BrushableBlockEntry> BRUSHABLE_BLOCKS = new HashMap<>();
//    public static final HashMap<BlockState, BlockState> CLEAN_ABLE_BLOCK_STATES = new HashMap<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BrushableBlocksResourceLoader());

//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState(), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 8), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 7), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 6));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 6), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 5));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 5), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 4));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 4), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 3));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 3), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 2));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 2), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1));
//        CLEAN_ABLE_BLOCK_STATES.put(Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1), Blocks.AIR.getDefaultState());

    }
}
