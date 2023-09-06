package to_binio.useful_brush.mixin.entity.wolf;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import to_binio.useful_brush.BrushableEntity;

@Mixin (WolfEntity.class)
public class WolfBrushableMixin implements BrushableEntity {
    @Override
    public boolean brush(PlayerEntity playerEntity) {
        WolfEntity wolf = (WolfEntity) (Object) this;

        if (!wolf.isOwner(playerEntity)) {
            return false;
        }

        double dogSize;
        double dogHeight;

        if (wolf.isBaby()) {
            dogSize = 0.3;
            dogHeight = 0.8;
        } else {
            dogSize = 0.4;
            dogHeight = 1.2;
        }

        wolf.heal(1);

        ClientWorld world = MinecraftClient.getInstance().world;

        double angle = Math.toRadians(wolf.getBodyYaw() + 90);

        double xOffset = Math.cos(angle) * dogSize;
        double zOffset = Math.sin(angle) * dogSize;

        world.addParticle(ParticleTypes.HEART, wolf.getX() + xOffset, wolf.getY() + dogHeight, wolf.getZ() + zOffset, 0, 4, 0);

        return true;
    }
}
