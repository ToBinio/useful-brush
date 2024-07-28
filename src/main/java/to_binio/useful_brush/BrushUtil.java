package to_binio.useful_brush;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

/**
 * Created: 14.07.24
 *
 * @author Tobias Frischmann
 */
public class BrushUtil {
    public static void handleBrushEvent(World world, ItemStack stack, PlayerEntity playerEntity,
            ActionResult brushResult) {
        if (brushResult == ActionResult.SUCCESS && !world.isClient()) {
            EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.damage(1, playerEntity, equipmentSlot);
        }

        if (brushResult == ActionResult.SUCCESS) {
            BrushCounter.clear(playerEntity.getId(), world);
        }
    }
}
