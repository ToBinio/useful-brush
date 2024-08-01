package to_binio.useful_brush.entities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
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
import java.util.function.Function;

import static to_binio.useful_brush.UsefulBrush.id;

public class BrushableEntitiesResourceLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return id("brushable_entities");
    }

    @Override
    public void reload(ResourceManager manager) {
        UsefulBrush.BASIC_BRUSHABLE_ENTITIES.clear();

        var brushable = manager.findAllResources("brushable/entity", identifier -> identifier.getPath()
                .endsWith(".json"));

        var count = 0;

        for (Map.Entry<Identifier, List<Resource>> resourceEntry : brushable.entrySet()) {
            for (Resource resource : resourceEntry.getValue()) {
                try (var input = resource.getInputStream()) {
                    String fileContent = new String(input.readAllBytes());
                    JsonObject data = JsonHelper.deserialize(fileContent);

                    var entity = get(data, resource.getPack().getName(), "id", jsonElement -> stringToEntityType(jsonElement.getAsString()), jsonElement -> String.format("could not find entity '%s'", jsonElement.getAsString()));

                    var particleBlock = get(data, resource.getPack().getName(), "particle_block", jsonElement -> stringToBlock(jsonElement.getAsString()), jsonElement -> String.format("could not find block '%s'", jsonElement.getAsString()));

                    var minParticleCount = get(data, resource.getPack().getName(), "min_particle_count", JsonElement::getAsInt, jsonElement -> String.format("could not parse min_particle_count '%s'", jsonElement.getAsString()));
                    var maxParticleCount = get(data, resource.getPack().getName(), "max_particle_count", JsonElement::getAsInt, jsonElement -> String.format("could not parse max_particle_count '%s'", jsonElement.getAsString()));

                    var height = get(data, resource.getPack().getName(), "height", JsonElement::getAsFloat, jsonElement -> String.format("could not parse height '%s'", jsonElement.getAsString()));
                    var babyHeight = get(data, resource.getPack().getName(), "baby_height", JsonElement::getAsFloat, jsonElement -> String.format("could not parse baby_height '%s'", jsonElement.getAsString()));

                    var lootTable = get(data, resource.getPack().getName(), "loot_table", jsonElement -> Identifier.tryParse(jsonElement.getAsString()), jsonElement -> String.format("could not parse loot_table '%s'", jsonElement.getAsString()));

                    var entry = new BrushableEntityEntry(new BlockStateParticleEffect(ParticleTypes.BLOCK, particleBlock.getDefaultState()), minParticleCount, maxParticleCount, height, babyHeight, lootTable);

                    var previous = UsefulBrush.BASIC_BRUSHABLE_ENTITIES.put(entity, entry);

                    if (previous != null) {
                        UsefulBrush.LOGGER.warn("%s -> %s got overwritten with %s".formatted(entity, previous.toString(), entry.toString()));
                    } else {
                        count++;
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (IllegalStateException e) {
                    UsefulBrush.LOGGER.error(e.getMessage());
                }
                UsefulBrush.LOGGER.info("Parsed brushable entities in {}", resourceEntry.getKey());
            }
        }

        UsefulBrush.LOGGER.info("Loaded {} brushable entities", count);
    }

    private <T> T get(JsonObject data, String packId, String key, Function<JsonElement, T> parse,
            Function<JsonElement, String> error) {
        if (!data.has(key)) {
            throw new IllegalStateException(String.format("no '%s' was set for '%s'", key, packId));
        }

        JsonElement element = data.get(key);
        try {
            var val = parse.apply(element);

            if (val == null) {
                throw new IllegalStateException(error.apply(element));
            }

            return val;
        } catch (Exception e) {
            throw new IllegalStateException(error.apply(element));
        }
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

    @Nullable
    private EntityType<?> stringToEntityType(String string) {
        var type = Registries.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(string));

        if (type.isEmpty()) {
            UsefulBrush.LOGGER.error("Unknown Entity '%s'".formatted(string));
            return null;
        }

        return type.get();
    }
}
