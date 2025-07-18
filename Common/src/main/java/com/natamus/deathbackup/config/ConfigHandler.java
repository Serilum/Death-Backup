package com.natamus.deathbackup.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.deathbackup.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean sendBackupReminderMessageToThoseWithAccessOnDeath = true;
	@Entry public static String backupReminderMessage = "A backup of your inventory before your death has been created and can be loaded with '/deathbackup load 0'.";

	public static void initConfig() {
		configMetaData.put("sendBackupReminderMessageToThoseWithAccessOnDeath", Arrays.asList(
			"When enabled, sends a message to a player when they die and have cheat-access that a backup has been created and can be loaded."
		));
		configMetaData.put("backupReminderMessage", Arrays.asList(
			"The message sent to players with cheat-access when 'sendBackupReminderMessageToThoseWithAccessOnDeath' is enabled."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}