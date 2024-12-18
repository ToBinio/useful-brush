package to_binio.useful_brush.blocks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import to_binio.useful_brush.UsefulBrush;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static to_binio.useful_brush.UsefulBrush.id;

/**
 * Created: 28.07.24
 *
 * @author Tobias Frischmann
 */
public class BrushableBlockResourceLoader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return id("brushable_blocks");
    }

    @Override
    public void reload(ResourceManager manager) {
        UsefulBrush.BASIC_BRUSHABLE_BLOCKS.clear();

        var brushable = manager.findResources("brushable/block", identifier -> identifier.getPath()
                .endsWith(".json"));

        var count = 0;

        for (Map.Entry<Identifier, Resource> resourceEntry : brushable.entrySet()) {
            Resource resource = resourceEntry.getValue();
            try (var input = resource.getInputStream()) {
                String fileContent = new String(input.readAllBytes());
                JsonObject data = JsonHelper.deserialize(fileContent);

                for (Map.Entry<String, JsonElement> entry : data.asMap().entrySet()) {
                    Block from = stringToBlock(entry.getKey());

                    if (from == null) {
                        UsefulBrush.LOGGER.error("could not find block '{}'", entry.getKey());
                        continue;
                    }

                    BrushableBlockEntry blockEntry = parseEntry(entry.getValue());

                    if (blockEntry == null) {
                        UsefulBrush.LOGGER.error("could parse data for '{}' - '{}'", entry.getKey(), entry.getValue());
                        continue;
                    }

                    var previous = UsefulBrush.BASIC_BRUSHABLE_BLOCKS.put(from, blockEntry);

                    if (previous != null) {
                        UsefulBrush.LOGGER.warn("%s -> %s got overwritten with %s".formatted(from, previous.block()
                                .toString(), blockEntry.block().toString()));
                    } else {
                        count++;
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            UsefulBrush.LOGGER.info("Parsed brushable blocks in {}", resourceEntry.getKey());
        }

        UsefulBrush.LOGGER.info("Loaded {} brushable blocks", count);
    }

    @Nullable
    private BrushableBlockEntry parseEntry(JsonElement data) {

        String to;
        Identifier lootTable = null;
        int brushCount = 1;

        if (data.isJsonObject()) {

            JsonObject value = data.getAsJsonObject();

            to = value.get("block").getAsString();

            if (value.has("loot_table")) {
                String lootTableString = value.get("loot_table").getAsString();
                lootTable = Identifier.tryParse(lootTableString);

                if (lootTable == null) {
                    UsefulBrush.LOGGER.error("Could not find loot_table '%s'".formatted(lootTableString));
                }
            }

            if (value.has("brush_count")) {
                try {
                    brushCount = value.get("brush_count").getAsInt();
                } catch (Exception e) {
                    UsefulBrush.LOGGER.error("Could not parse brush_count '%s'".formatted(value.get("brush_count")
                            .getAsString()));
                }
            }

        } else {
            to = data.getAsString();
        }

        Block block = stringToBlock(to);

        if (block == null) return null;

        return new BrushableBlockEntry(block, lootTable, brushCount);
    }

    @Nullable
    private Block stringToBlock(String string) {
        var block = Registries.BLOCK.getOptionalValue(Identifier.tryParse(string));

        if (block.isEmpty()) {
            UsefulBrush.LOGGER.error("Unknown block '%s'".formatted(string));
            return null;
        }

        return block.get();
    }
}
