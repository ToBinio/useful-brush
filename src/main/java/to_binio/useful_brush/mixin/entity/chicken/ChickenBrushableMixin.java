package to_binio.useful_brush.mixin.entity.chicken;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
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

@Mixin (ChickenEntity.class)
public class ChickenBrushableMixin implements BrushableEntity {

    @Override
    public boolean brush(PlayerEntity playerEntity, Vec3d brushLocation) {
        ChickenEntity chicken = (ChickenEntity) (Object) this;
        BrushCount brushCount = (BrushCount) chicken;
        Random random = Random.create();
        World world = MinecraftClient.getInstance().world;

        BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CALCITE.getDefaultState());

        var chickenHeight = chicken.isBaby() ? 0.1 : 0.3;
        int particleCount = (int) (random.nextBetweenExclusive(2, 5) * (chicken.isBaby() ? 0.5 : 1));

        for (int k = 0; k < particleCount; ++k) {
            world.addParticle(blockStateParticleEffect, brushLocation.x, brushLocation.y + chickenHeight, brushLocation.z, world.getRandom().nextDouble() - 0.5, world.getRandom().nextDouble(), world.getRandom().nextDouble() - .5);
        }

        if (brushCount.getBrushCount() >= UsefulBrush.CHICKEN_MAX_BRUSH_COUNT * (chicken.isBaby() ? 0.5 : 1) || Random.create().nextBetween(0, 3) != 0) {
            return false;
        }

        chicken.dropStack(new ItemStack(Items.FEATHER.asItem()), (float) chickenHeight);
        chicken.getWorld().playSound(playerEntity, chicken.getBlockPos(), SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.BLOCKS);

        brushCount.setBrushCount(brushCount.getBrushCount() + 1);

        return true;
    }
}
