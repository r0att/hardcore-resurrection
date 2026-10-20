package pl.r0att.hardcoreresurrection.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDeathMixin {
    @Inject(
        method = "dropExperience",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventExperienceDrops(CallbackInfo info) {
        if ((Object) this instanceof Player) {
            info.cancel();
        }
        System.out.println("dropExperience intercepted");
    }
}