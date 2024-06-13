package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to_binio.useful_brush.blocks.BrushableBlockEntry;
import to_binio.useful_brush.blocks.BrushableBlockEvents;
import to_binio.useful_brush.blocks.BrushableBlocksResourceLoader;
import to_binio.useful_brush.config.UsefulBrushConfig;
import to_binio.useful_brush.entities.BrushableEntityEvents;

import java.util.HashMap;

public class UsefulBrush implements ModInitializer {


    public static final String MOD_ID = "useful_brush";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<Block, BrushableBlockEntry> BRUSHABLE_BLOCKS = new HashMap<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BrushableBlocksResourceLoader());

        UsefulBrushConfig.initialize();

        BrushableEntityEvents.register();
        BrushableBlockEvents.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

}
