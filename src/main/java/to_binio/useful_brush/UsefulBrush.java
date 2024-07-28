package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to_binio.useful_brush.blocks.BrushableBlockEntry;
import to_binio.useful_brush.blocks.BrushableBlockEvents;
import to_binio.useful_brush.entities.BrushableEntitiesResourceLoader;
import to_binio.useful_brush.entities.BrushableEntityEntry;
import to_binio.useful_brush.entities.BrushableEntityEvents;
import to_binio.useful_brush.blocks.BrushableBlockResourceLoader;

import java.util.HashMap;

public class UsefulBrush implements ModInitializer {


    public static final String MOD_ID = "useful_brush";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HashMap<Block, BrushableBlockEntry> BASIC_BRUSHABLE_BLOCKS = new HashMap<>();
    public static final HashMap<EntityType<?>, BrushableEntityEntry> BASIC_BRUSHABLE_ENTITIES = new HashMap<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new BrushableEntitiesResourceLoader());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BrushableBlockResourceLoader());

        BASIC_BRUSHABLE_ENTITIES.put(EntityType.ARMADILLO, new BrushableEntityEntry(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SAND.getDefaultState()), 4, 6, 0.4f, 0.2f, id("chicken")));

        BrushableEntityEvents.register();
        BrushableBlockEvents.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

}
