package cn.yufurry.mixin.client;

import cn.yufurry.client.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    private void soarboisterously$suppressFallDamage(double fallDistance, float fallDamageMultiplier, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof Player && ModConfig.INSTANCE.enabled && !ModConfig.INSTANCE.fallDamage) {
            cir.setReturnValue(0);
        }
    }
}
