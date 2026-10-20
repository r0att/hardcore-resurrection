package pl.r0att.hardcoreresurrection.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerDeathMixin {
    @Inject(
        method = "dropEquipment",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventEquipmentDrops(CallbackInfo info) {
        info.cancel();
    }
}