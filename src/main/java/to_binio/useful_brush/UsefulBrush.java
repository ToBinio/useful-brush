package to_binio.useful_brush;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsefulBrush implements ModInitializer {


    public static final String MOD_ID = "useful_brush";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int SHEEP_MAX_BRUSH_COUNT = 3;
    public static final int CHICKEN_MAX_BRUSH_COUNT = 2;

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
