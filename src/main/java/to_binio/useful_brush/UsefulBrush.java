package to_binio.useful_brush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import to_binio.useful_brush.blocks.BrushableBlockEntry;
import to_binio.useful_brush.blocks.BrushableBlockEvents;
import to_binio.useful_brush.blocks.BrushableBlocksResourceLoader;
import to_binio.useful_brush.config.UsefulBrushConfig;
import to_binio.useful_brush.entities.BrushableEntityEntry;
import to_binio.useful_brush.entities.BrushableEntityEvents;

import javax.swing.text.html.parser.Entity;
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

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BrushableBlocksResourceLoader());

        BASIC_BRUSHABLE_ENTITIES.put(EntityType.CHICKEN, new BrushableEntityEntry(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CALCITE.getDefaultState()), 2, 5, 0.3f, 0.1f, entity -> Items.FEATHER, 5));
        BASIC_BRUSHABLE_ENTITIES.put(EntityType.ARMADILLO, new BrushableEntityEntry(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SAND.getDefaultState()), 4, 6, 0.4f, 0.2f, entity -> Items.ARMADILLO_SCUTE, 3));

        BASIC_BRUSHABLE_ENTITIES.put(EntityType.MOOSHROOM, new BrushableEntityEntry(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.RED_MUSHROOM_BLOCK.getDefaultState()), 7, 12, 0.8f, 0.3f, entity -> {
            if (((MooshroomEntity) entity).getVariant() == MooshroomEntity.Type.BROWN) {
                return Items.BROWN_MUSHROOM;
            } else {
                return Items.RED_MUSHROOM;
            }
        }, 10));

        UsefulBrushConfig.initialize();

        BrushableEntityEvents.register();
        BrushableBlockEvents.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

}
