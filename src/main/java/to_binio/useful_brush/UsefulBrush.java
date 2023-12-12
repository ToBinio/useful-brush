package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to_binio.useful_brush.blocks.BrushableBlockEntry;
import to_binio.useful_brush.blocks.BrushableBlockEvents;
import to_binio.useful_brush.blocks.BrushableBlocksResourceLoader;
import to_binio.useful_brush.config.UsefulBrushConfig;
import to_binio.useful_brush.entities.BrushableEntityEvents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UsefulBrush implements ModInitializer {


    public static final String MOD_ID = "useful_brush";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<Block, BrushableBlockEntry> BRUSHABLE_BLOCKS = new HashMap<>();
    public static final Set<Block> BRUSHABLE_OUTLINE_BLOCKS = new HashSet<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BrushableBlocksResourceLoader());

        BRUSHABLE_OUTLINE_BLOCKS.add(Blocks.SNOW);

        UsefulBrushConfig.initialize();

        BrushableEntityEvents.register();
        BrushableBlockEvents.register();
    }
}
