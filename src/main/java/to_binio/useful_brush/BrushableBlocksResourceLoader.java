package to_binio.useful_brush;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrushableBlocksResourceLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier(UsefulBrush.MOD_ID, "brushable_blocks");
    }

    @Override
    public void reload(ResourceManager manager) {
        UsefulBrush.BRUSHABLE_BLOCKS.clear();

        Map<Identifier, List<Resource>> brushable = manager.findAllResources("brushables", identifier -> identifier.getPath().endsWith(".json"));

        var count = 0;

        for (List<Resource> resources : brushable.values()) {
            for (Resource resource : resources) {

                try (var input = resource.getInputStream()) {
                    String fileContent = new String(input.readAllBytes());
                    JsonObject data = JsonHelper.deserialize(fileContent);

                    for (Map.Entry<String, JsonElement> entry : data.asMap().entrySet()) {
                        Block from = stringToBlock(entry.getKey());
                        BrushableBlockEntry blockEntry = parseEntry(entry.getValue());

                        if (blockEntry == null || from == null) continue;

                        UsefulBrush.BRUSHABLE_BLOCKS.put(from, blockEntry);
                        count++;
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        UsefulBrush.LOGGER.info("Loaded " + count + " brushable blocks");
    }

    @Nullable
    private BrushableBlockEntry parseEntry(JsonElement data) {

        String to;
        Identifier loot_table = null;

        if (data.isJsonObject()) {

            JsonObject value = data.getAsJsonObject();

            to = value.get("block").getAsString();

            if (value.has("loot_table")) {
                String lootTableString = value.get("loot_table").getAsString();
                loot_table = Identifier.tryParse(lootTableString);

                if (loot_table == null) {
                    UsefulBrush.LOGGER.error("Could not find loot_table '%s'".formatted(lootTableString));
                }
            }

        } else {
            to = data.getAsString();
        }

        Block block = stringToBlock(to);

        if (block == null) return null;

        return new BrushableBlockEntry(block, loot_table);
    }

    @Nullable
    private Block stringToBlock(String string) {
        var block = Registries.BLOCK.getOrEmpty(Identifier.tryParse(string));

        if (block.isEmpty()) {
            UsefulBrush.LOGGER.error("Unknown block '%s'".formatted(string));
            return null;
        }

        return block.get();
    }
}
