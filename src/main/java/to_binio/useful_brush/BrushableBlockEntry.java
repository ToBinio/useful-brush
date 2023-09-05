package to_binio.useful_brush;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public record BrushableBlockEntry(Block block, Identifier lootTable) {
}
