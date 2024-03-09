package com.natamus.deathbackup.events;

import com.natamus.collective.functions.DateFunctions;
import com.natamus.collective.functions.PlayerFunctions;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.deathbackup.config.ConfigHandler;
import com.natamus.deathbackup.util.Util;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DeathBackupEvent {

	public static void onPlayerDeath(ServerLevel serverworld, ServerPlayer player) {
		String playername = player.getName().getString().toLowerCase();
		
		String gearstring = PlayerFunctions.getPlayerGearString(player);
		if (gearstring.equals("")) {
			return;
		}
		
		String nowstring = DateFunctions.getNowInYmdhis();
		Util.writeGearStringToFile(serverworld, playername, nowstring, gearstring);
		
		if (ConfigHandler.sendBackupReminderMessageToThoseWithAccessOnDeath) {
			if (player.hasPermissions(2)) {
				MessageFunctions.sendMessage(player, ConfigHandler.backupReminderMessage, ChatFormatting.DARK_GRAY);
			}
		}
	}
}
