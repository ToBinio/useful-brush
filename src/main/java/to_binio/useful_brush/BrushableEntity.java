package to_binio.useful_brush;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;

public interface BrushableEntity {

    boolean brush(PlayerEntity playerEntity, Vec3d brushLocation);
}
