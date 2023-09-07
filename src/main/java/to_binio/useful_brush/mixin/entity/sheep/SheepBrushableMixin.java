package to_binio.useful_brush.mixin.entity.sheep;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushableEntity;
import to_binio.useful_brush.BrushCount;
import to_binio.useful_brush.UsefulBrush;
import to_binio.useful_brush.mixin.BrushItemMixin;

@Mixin (SheepEntity.class)
public class SheepBrushableMixin implements BrushableEntity {

    @Override
    public boolean brush(PlayerEntity playerEntity, Vec3d brushLocation) {
        SheepEntity sheep = (SheepEntity) (Object) this;
        BrushCount brushCount = (BrushCount) sheep;
        Random random = Random.create();
        World world = MinecraftClient.getInstance().world;

        BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.getDefaultState());

        int particleCount = random.nextBetweenExclusive(7, 12);

        for (int k = 0; k < particleCount; ++k) {
            world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + 0.8, brushLocation.z, 3.0 * world.getRandom().nextDouble() - 1.5, 2.0 * world.getRandom().nextDouble(), 3.0 * world.getRandom().nextDouble() - 1.5);
        }

        if (brushCount.getBrushCount() >= UsefulBrush.SHEEP_MAX_BRUSH_COUNT || random.nextBetween(0, 5) != 0) {
            return false;
        }

        sheep.dropItem(Items.STRING.asItem(), 1);
        world.playSound(playerEntity, sheep.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        brushCount.setBrushCount(brushCount.getBrushCount() + 1);

        return true;
    }
}
