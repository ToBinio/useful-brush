package to_binio.useful_brush.entities;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Created: 14.07.24
 *
 * @author Tobias Frischmann
 */
public record BrushableEntityEntry(ParticleEffect particleEffect, int minParticleCount, int maxParticleCount,
                                   float height, float babyHeight, Identifier lootTable) {
}
