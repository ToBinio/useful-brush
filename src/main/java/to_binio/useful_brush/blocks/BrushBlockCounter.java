package to_binio.useful_brush.blocks;

import net.minecraft.util.math.BlockPos;
import to_binio.useful_brush.UsefulBrush;

import java.util.HashMap;
import java.util.Map;

public class BrushBlockCounter {

    private final static Map<Integer, Entry> clientTimes = new HashMap<>();
    private final static Map<Integer, Entry> serverTimes = new HashMap<>();

    public static int decreaseCount(BlockPos pos, int playerID, int startCount, boolean isClient) {
        return decreaseCount(pos, playerID, startCount, isClient ? clientTimes : serverTimes);
    }

    private static int decreaseCount(BlockPos pos, int playerID, int startCount, Map<Integer, Entry> times) {
        Entry entry = times.getOrDefault(playerID, new Entry(pos, startCount));
        int count = entry.value - 1;

        if (!entry.pos.equals(pos)) {
            count = startCount - 1;
        }

        times.put(playerID, new Entry(pos, count));

        return count;
    }

    public static int get(BlockPos pos, int playerID, int defaultValue, boolean isClient) {
        return get(pos, playerID, defaultValue, isClient ? clientTimes : serverTimes);
    }

    private static int get(BlockPos pos, int playerID, int defaultValue, Map<Integer, Entry> times) {

        Entry entry = times.get(playerID);

        if (entry == null) {
            return defaultValue;
        }

        if (!entry.pos.equals(pos)) {
            clear(playerID, times);
            return defaultValue;
        }

        return entry.value;
    }

    public static boolean clear(int playerID, boolean isClient) {
        return clear(playerID, isClient ? clientTimes : serverTimes);
    }

    private static boolean clear(int playerID, Map<Integer, Entry> times) {
        return times.remove(playerID) != null;
    }

    record Entry(BlockPos pos, Integer value) {

    }
}
