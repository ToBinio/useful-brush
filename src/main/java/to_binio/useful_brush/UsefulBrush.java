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
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsefulBrush implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("useful_brush");

    public static final int SHEEP_MAX_BRUSH_COUNT = 3;
    public static final int CHICKEN_MAX_BRUSH_COUNT = 2;

    public static final HashMap<Block, BrushableBlockEntry> BRUSHABLE_BLOCKS = new HashMap<>();
//    public static final HashMap<BlockState, BlockState> CLEAN_ABLE_BLOCK_STATES = new HashMap<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return new Identifier("useful_brush", "brushable_blocks");
            }

            @Override
            public void reload(ResourceManager manager) {
                BRUSHABLE_BLOCKS.clear();

                Map<Identifier, List<Resource>> brushable = manager.findAllResources("brushables", identifier -> identifier.getPath().endsWith(".json"));

                var count = 0;

                for (List<Resource> resources : brushable.values()) {
                    for (Resource resource : resources) {

                        try (var input = resource.getInputStream()) {
                            String string = new String(input.readAllBytes());
                            JsonObject data = JsonHelper.deserialize(string);

                            for (Map.Entry<String, JsonElement> entry : data.asMap().entrySet()) {
                                var from = entry.getKey();
                                String to;
                                Identifier loot_table = null;

                                if (entry.getValue().isJsonObject()) {

                                    JsonObject value = entry.getValue().getAsJsonObject();

                                    to = value.get("block").getAsString();

                                    if (value.has("loot_table")) {
                                        String lootTableString = value.get("loot_table").getAsString();
                                        loot_table = Identifier.tryParse(lootTableString);

                                        if (loot_table == null) {
                                            LOGGER.error("Could not find loot_table '%s'".formatted(lootTableString));
                                        }
                                    }

                                } else {
                                    to = entry.getValue().getAsString();
                                }

                                var itemFrom = Registries.BLOCK.getOrEmpty(Identifier.tryParse(from)).orElseThrow(() -> new JsonSyntaxException("Unknown block '" + from + "'"));
                                var itemTo = Registries.BLOCK.getOrEmpty(Identifier.tryParse(to)).orElseThrow(() -> new JsonSyntaxException("Unknown block '" + to + "'"));

                                BRUSHABLE_BLOCKS.put(itemFrom, new BrushableBlockEntry(itemTo, loot_table));
                                count++;
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                LOGGER.info("Loaded " + count + " brushable blocks");
            }
        });

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
