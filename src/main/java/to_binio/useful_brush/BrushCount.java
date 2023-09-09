package to_binio.useful_brush;

import org.apache.commons.lang3.NotImplementedException;

public interface BrushCount {

    default int getBrushCount() {
        throw new NotImplementedException();
    }

    default void setBrushCount(int brushCount) {
        throw new NotImplementedException();
    }
}
