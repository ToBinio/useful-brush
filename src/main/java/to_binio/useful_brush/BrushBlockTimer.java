package to_binio.useful_brush;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class BrushBlockTimer {

    private final static Map<Integer, Entry> clientTimes = new HashMap<>();
    private final static Map<Integer, Entry> serverTimes = new HashMap<>();

    public static int decreaseCount(BlockPos pos, int playerID, int startCount, boolean isClient) {
        UsefulBrush.LOGGER.info(isClient + " decrease");
        return decreaseCount(pos, playerID, startCount, isClient ? clientTimes : serverTimes);
    }

    private static int decreaseCount(BlockPos pos, int playerID, int startCount, Map<Integer, Entry> times) {
        Entry entry = times.getOrDefault(playerID, new Entry(pos, startCount));
        int count = entry.value - 1;

        if (!entry.pos.equals(pos)) {
            count = startCount - 1;
        }

        times.put(playerID, new Entry(pos, count));
        UsefulBrush.LOGGER.info(count + "");

        return count;
    }

    public static int get(BlockPos pos, int playerID, int defaultValue, boolean isClient) {
        UsefulBrush.LOGGER.info(isClient + " get");
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

        UsefulBrush.LOGGER.info(entry.value + "");
        return entry.value;
    }

    public static boolean clear(int playerID, boolean isClient) {
        UsefulBrush.LOGGER.info(isClient + " clear");
        return clear(playerID, isClient ? clientTimes : serverTimes);
    }

    private static boolean clear(int playerID, Map<Integer, Entry> times) {
        return times.remove(playerID) != null;
    }

    record Entry(BlockPos pos, Integer value) {

    }
}
