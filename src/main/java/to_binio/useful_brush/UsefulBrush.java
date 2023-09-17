package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to_binio.useful_brush.config.SimpleConfig;
import to_binio.useful_brush.config.UsefulBrushConfig;

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

        BrushableEntities.register();
        BrushableBlocks.register();
    }
}
