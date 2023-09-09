package to_binio.useful_brush.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BrushEntityEvent {

    private static final Map<Class<? extends Entity>, Event<BrushEntity>> EVENTS = new HashMap<>();

    public static Event<BrushEntity> getEvent(Class<? extends Entity> entityClass) {
        Event<BrushEntity> brushEntityEvent = EVENTS.get(entityClass);

        if (brushEntityEvent == null) {
            Event<BrushEntity> event = EventFactory.createArrayBacked(BrushEntity.class, brushEntities -> (entity, playerEntity, brushLocation) -> {
                for (BrushEntity brushEntity : brushEntities) {

                    ActionResult result = brushEntity.brush(entity, playerEntity, brushLocation);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

            EVENTS.put(entityClass, event);
            brushEntityEvent = event;
        }

        return brushEntityEvent;
    }


    @FunctionalInterface
    public interface BrushEntity {
        ActionResult brush(Entity entity, PlayerEntity playerEntity, Vec3d brushLocation);
    }
}
