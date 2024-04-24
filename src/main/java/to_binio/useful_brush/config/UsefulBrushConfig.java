package to_binio.useful_brush.config;

import to_binio.useful_brush.UsefulBrush;

public class UsefulBrushConfig {

    public static final UsefulBrushConfig INSTANCE = new UsefulBrushConfig();
    public final int SHEEP_DROP_COUNT;
    public final int CHICKEN_DROP_COUNT;
    public final int MOOSHROOM_DROP_COUNT;
    public final int ARMADILLO_DROP_COUNT;

    public final int BASE_BRUSH_COUNT;

    public UsefulBrushConfig() {
        var config = SimpleConfig.of(UsefulBrush.MOD_ID).provider(this::provider).request();

        SHEEP_DROP_COUNT = config.getOrDefault("drop.sheep", 3);
        CHICKEN_DROP_COUNT = config.getOrDefault("drop.chicken", 2);
        MOOSHROOM_DROP_COUNT = config.getOrDefault("drop.mooshroom", 1);
        ARMADILLO_DROP_COUNT = config.getOrDefault("drop.armadillo", 4);

        BASE_BRUSH_COUNT = config.getOrDefault("brush.count", 15);
    }

    public static void initialize() {
    }

    private String provider(String filename) {
        return """
                # This is the configuration file for UsefulBrush.
                                
                # How many item drops until brush.count is required for the next drop
                # drop.sheep = 3
                # drop.chicken = 2
                # drop.mooshroom = 1
                # drop.armadillo = 4
                                
                # How often you have to brush per count (percentage 15 = 7% chance to drop)
                # brush.count = 15
                """;
    }

}
