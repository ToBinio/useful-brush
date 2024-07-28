package to_binio.useful_brush;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import to_binio.useful_brush.blocks.BrushableBlocks;

import java.util.HashMap;
import java.util.Map;

public class BrushCounter {

    private final static Map<Integer, Entry> clientTimes = new HashMap<>();
    private final static Map<Integer, Entry> serverTimes = new HashMap<>();

    public static void brushBlock(BlockPos pos, int playerID, World world) {
        var times = world.isClient() ? clientTimes : serverTimes;

        Entry entry = times.getOrDefault(playerID, new BlockEntry(pos, 0));
        int count = entry.value() + 1;

        switch (entry) {
            case BlockEntry blockEntry: {
                if (!blockEntry.pos.equals(pos)) {
                    clear(playerID, world);
                    count = 1;
                }
                break;
            }
            case EntityEntry ignored: {
                clear(playerID, world);
                count = 1;
                break;
            }
            default:
        }

        times.put(playerID, new BlockEntry(pos, count));
    }

    public static void brushEntity(int entityId, int playerID, World world) {
        var times = world.isClient() ? clientTimes : serverTimes;

        Entry entry = times.getOrDefault(playerID, new EntityEntry(entityId, 0));
        int count = entry.value() + 1;

        switch (entry) {
            case BlockEntry ignore: {
                clear(playerID, world);
                count = 1;
                break;
            }
            case EntityEntry entityEntry: {
                if (entityEntry.id != entityId) {
                    clear(playerID, world);
                    count = 1;
                }
                break;
            }
            default:
        }

        times.put(playerID, new EntityEntry(entityId, count));
    }

    public static int get(int playerID, boolean isClient) {
        var times = isClient ? clientTimes : serverTimes;
        var entry = times.get(playerID);

        if (entry == null) {
            return 0;
        }

        return entry.value();
    }

    public static void clear(int playerID, World world) {
        BrushableBlocks.clear(playerID, world);

        var times = world.isClient() ? clientTimes : serverTimes;
        times.remove(playerID);
    }

    interface Entry {
        int value();
    }

    record BlockEntry(BlockPos pos, int value) implements Entry {
    }

    record EntityEntry(int id, int value) implements Entry {
    }
}
