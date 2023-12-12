package com.natamus.deathbackup;

import com.natamus.collective.check.RegisterMod;
import com.natamus.deathbackup.cmds.CommandDeathBackup;
import com.natamus.deathbackup.events.DeathBackupEvent;
import com.natamus.deathbackup.util.Reference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			CommandDeathBackup.register(dispatcher);
		});

		ServerPlayerEvents.ALLOW_DEATH.register((ServerPlayer player, DamageSource damageSource, float damageAmount) -> {
				DeathBackupEvent.onPlayerDeath((ServerLevel)player.getCommandSenderWorld(), player);
			return true;
		});
	}

	private static void setGlobalConstants() {

	}
}
