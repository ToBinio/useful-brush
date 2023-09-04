package to_binio.useful_brush.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import to_binio.useful_brush.BrushAble;

@Mixin (WolfEntity.class)
public class WolfBrushAbleMixin implements BrushAble {
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
