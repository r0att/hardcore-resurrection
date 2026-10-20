package pl.r0att.hardcoreresurrection;

import net.fabricmc.api.ModInitializer;
import pl.r0att.hardcoreresurrection.data.DeadPlayerData;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardcoreResurrection implements ModInitializer {
	public static final String MOD_ID = "hardcore-resurrection";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		DeathHandler.initialize();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			DeadPlayerData.get(server.overworld());
		});

		LOGGER.info("Hardcore Resurrection initialized");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
