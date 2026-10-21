package pl.r0att.hardcoreresurrection.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import pl.r0att.hardcoreresurrection.HardcoreResurrection;
import java.util.Optional;
import java.util.UUID;

public final class DeadPlayerData extends SavedData {
    private static final String DATA_NAME =
        "hardcore_resurrection_dead_players";

    private static final SavedData.Factory<DeadPlayerData> FACTORY =
        new SavedData.Factory<>(
            DeadPlayerData::new,
            DeadPlayerData::load,
            null
        );

    private final CompoundTag deadPlayers = new CompoundTag();

    public DeadPlayerData() {}

    public static DeadPlayerData get(ServerLevel level) {
        DeadPlayerData data = level.getDataStorage().computeIfAbsent(
            FACTORY,
            DATA_NAME
        );

        HardcoreResurrection.LOGGER.info(
            "Loaded death snapshots: {}",
            data.deadPlayers.getAllKeys()
        );

        return data;
    }

    public static DeadPlayerData load(
        CompoundTag tag,
        HolderLookup.Provider registries
    ) {
        DeadPlayerData data = new DeadPlayerData();

        if (tag.contains("dead_players")) {
            data.deadPlayers.merge(tag.getCompound("dead_players"));
        }

        return data;
    }

    public void recordDeath(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        HolderLookup.Provider registries = level.registryAccess();

        CompoundTag playerData = new CompoundTag();
        ListTag inventory = new ListTag();

        for (int slot = 0;
             slot < player.getInventory().getContainerSize();
             slot++) {

            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            CompoundTag itemData = new CompoundTag();
            itemData.putInt("slot", slot);
            itemData.put("stack", stack.save(registries));

            inventory.add(itemData);

            HardcoreResurrection.LOGGER.info(
                "Snapshot item: {} x{} (slot {})",
                stack.getItem(),
                stack.getCount(),
                slot
            );
        }

        playerData.putString("name", player.getGameProfile().getName());
        playerData.putInt("xp_level", player.experienceLevel);
        playerData.putBoolean(
            "keep_inventory",
            level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
        );
        playerData.put("inventory", inventory);

        deadPlayers.put(player.getUUID().toString(), playerData);
        setDirty();

        HardcoreResurrection.LOGGER.info(
            "Saved death snapshot for player {}: XP level={}, inventory slots={}, keepInventory={}",
            player.getGameProfile().getName(),
            player.experienceLevel,
            inventory.size(),
            level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
        );
    }

    @Override
    public CompoundTag save(
        CompoundTag tag,
        HolderLookup.Provider registriesProvider
    ) {
        tag.put("dead_players", deadPlayers.copy());
        return tag;
    }

    public CompoundTag getDeadPlayers() {
        return deadPlayers;
    }

    public Optional<CompoundTag> getSnapshot(UUID playerId) {
        String key = playerId.toString();

        if (!deadPlayers.contains(key)) {
            return Optional.empty();
        }

        return Optional.of(deadPlayers.getCompound(key).copy());
    }
}