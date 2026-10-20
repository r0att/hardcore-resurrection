package pl.r0att.hardcoreresurrection;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import pl.r0att.hardcoreresurrection.data.DeadPlayerData;

public final class DeathHandler {
    private DeathHandler() {}

    public static void initialize() {
        ServerLivingEntityEvents.ALLOW_DEATH.register(
            (entity, damageSource, damageAmount) -> {
                if (entity instanceof ServerPlayer player) {
                    DeadPlayerData.get(player.serverLevel())
                        .recordDeath(player);
                }

                return true;
            }
        );
    }
}
