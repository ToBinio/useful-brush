package to_binio.useful_brush.mixin.entity.mooshroom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushCount;
import to_binio.useful_brush.BrushableEntity;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.mixin.entity.sheep.SheepAccessor;

/**
 * Created: 08/09/2023
 * @author Tobias Frischmann
 */

@Mixin (MooshroomEntity.class)
public class MooshroomBrushableMixin implements BrushableEntity {

    public boolean brush(PlayerEntity playerEntity, Vec3d brushLocation) {
        MooshroomEntity mooshroom = (MooshroomEntity) (Object) this;
        BrushCount brushCount = (BrushCount) mooshroom;
        Random random = mooshroom.getRandom();
        World world = mooshroom.getWorld();

        var mooshroomHeight = mooshroom.isBaby() ? 0.3 : 0.8;

        BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.RED_MUSHROOM_BLOCK.getDefaultState());

        int particleCount = (int) (random.nextBetweenExclusive(7, 12) * (mooshroom.isBaby() ? 0.5 : 1));

        for (int k = 0; k < particleCount; ++k) {
            world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + mooshroomHeight, brushLocation.z, 3.0 * world.getRandom().nextDouble() - 1.5, 2.0 * world.getRandom().nextDouble(), 3.0 * world.getRandom().nextDouble() - 1.5);
        }

        if (brushCount.getBrushCount() >= UsefulBrush.MOOSHROOM_MAX_BRUSH_COUNT || random.nextBetween(0, 10) != 0) {
            return false;
        }

        if (random.nextBetween(0, 5) == 0) {
            mooshroom.dropStack(new ItemStack(Items.BROWN_MUSHROOM.asItem()), (float) mooshroomHeight);
        } else {
            mooshroom.dropStack(new ItemStack(Items.RED_MUSHROOM.asItem()), (float) mooshroomHeight);
        }

        world.playSound(playerEntity, mooshroom.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        brushCount.setBrushCount(brushCount.getBrushCount() + 1);

        return true;
    }

}
