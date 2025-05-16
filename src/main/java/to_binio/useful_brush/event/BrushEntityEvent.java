package to_binio.useful_brush.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class BrushEntityEvent {

    private static final Map<Class<? extends Entity>, Event<BrushEntity>> EVENTS = new HashMap<>();
    private static final Map<Class<? extends Entity>, Event<BrushEntity>> VISUAL_EVENTS = new HashMap<>();

    public static Event<BrushEntity> getEvent(Class<? extends Entity> entityClass) {
        Event<BrushEntity> brushEntityEvent = EVENTS.get(entityClass);

        if (brushEntityEvent == null) {
            Event<BrushEntity> event = createEvent();

            EVENTS.put(entityClass, event);
            brushEntityEvent = event;
        }

        return brushEntityEvent;
    }

    public static Event<BrushEntity> getVisualEvent(Class<? extends Entity> entityClass) {
        Event<BrushEntity> brushEntityEvent = VISUAL_EVENTS.get(entityClass);

        if (brushEntityEvent == null) {
            Event<BrushEntity> event = createEvent();

            VISUAL_EVENTS.put(entityClass, event);
            brushEntityEvent = event;
        }

        return brushEntityEvent;
    }

    private static Event<BrushEntity> createEvent() {
        return EventFactory.createArrayBacked(BrushEntity.class,
                brushEntities -> (entity, playerEntity, brushLocation) -> {
                    for (BrushEntity brushEntity : brushEntities) {

                        ActionResult result = brushEntity.brush(entity, playerEntity, brushLocation);

                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });
    }


    @FunctionalInterface
    public interface BrushEntity {
        ActionResult brush(Entity entity, PlayerEntity playerEntity, Vec3d brushLocation);
    }
}
