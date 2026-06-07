package com.natamus.deathbackup.cmds;
import com.natamus.deathbackup.util.Reference;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.natamus.collective.functions.DateFunctions;
import com.natamus.collective.functions.PlayerFunctions;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.deathbackup.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class CommandDeathBackup {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("deathbackup").requires((iCommandSender) -> iCommandSender.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
			.executes((command) -> {
				CommandSourceStack source = command.getSource();
				MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.usage", ChatFormatting.DARK_GREEN, Reference.NAME);
				MessageFunctions.sendMessage(source, "/deathbackup list - Lists all available backups.", ChatFormatting.DARK_GREEN);
				MessageFunctions.sendMessage(source, "/deathbackup load <index> - Loads the backup with <index> from '/deathbackup list'. Index 0 is the last death.", ChatFormatting.DARK_GREEN);
				return 1;
			})
			.then(Commands.literal("list")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();
				Player player;
				try {
					player = source.getPlayerOrException();
				}
				catch (CommandSyntaxException ex) {
					MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
					return 1;
				}
				
				Level world = player.level();
				if (world.isClientSide()) {
					MessageFunctions.sendTranslatableMessage(source, "[Error] ", "collective.deathbackup.message.worldremoteunable", ChatFormatting.RED);
					return 1;
				}
				
				if (!(world instanceof ServerLevel)) {
					MessageFunctions.sendTranslatableMessage(source, "[Error] ", "collective.deathbackup.message.cannotfindworld", ChatFormatting.RED);
					return 1;
				}
				
				String playername = player.getName().getString().toLowerCase();
				ServerLevel serverworld = (ServerLevel)world;
				
				List<String> backups = Util.getListOfBackups(serverworld, playername);

				MessageFunctions.sendTranslatableMessage(source, "collective.deathbackup.message.lastdeathbackups", true, ChatFormatting.DARK_GREEN);
				
				int index = 0;
				for (String ymdhis : backups) {
					MessageFunctions.sendMessage(source, " " + index + ": " + DateFunctions.ymdhisToReadable(ymdhis), ChatFormatting.DARK_GREEN);
					index += 1;
					if (index == 10) {
						break;
					}
				}
				
				MessageFunctions.sendTranslatableMessage(source, "collective.deathbackup.message.loadbackupdeathbackup", ChatFormatting.YELLOW);
				return 1;
			}))
			.then(Commands.literal("load")
			.then(Commands.argument("backup_index", IntegerArgumentType.integer())
			.executes((command) -> {
				CommandSourceStack source = command.getSource();
				Player player;
				try {
					player = source.getPlayerOrException();
				}
				catch (CommandSyntaxException ex) {
					MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
					return 1;
				}
				
				Level world = player.level();
				if (world.isClientSide()) {
					MessageFunctions.sendTranslatableMessage(source, "[Error] ", "collective.deathbackup.message.worldremoteunable", ChatFormatting.RED);
					return 1;
				}
				
				if (!(world instanceof ServerLevel)) {
					MessageFunctions.sendTranslatableMessage(source, "[Error] ", "collective.deathbackup.message.cannotfindworld", ChatFormatting.RED);
					return 1;
				}
				
				String playername = player.getName().getString().toLowerCase();
				ServerLevel serverworld = (ServerLevel)world;
				
				List<String> backups = Util.getListOfBackups(serverworld, playername);
				
				int amount = IntegerArgumentType.getInteger(command, "backup_index");
				if (amount < 0 || amount >= backups.size()) {
					MessageFunctions.sendTranslatableMessage(source, "collective.deathbackup.message.indexinvalid", ChatFormatting.RED, amount);
					return 0;
				}
				
				String backupfilename = backups.get(amount);
				String gearstring = Util.getGearStringFromFile(serverworld, playername, backupfilename);
				if (gearstring.equals("")) {
					MessageFunctions.sendTranslatableMessage(source, "[Error] ", "collective.deathbackup.message.unablereadbackup", ChatFormatting.RED);
					return 0;
				}

				PlayerFunctions.setPlayerGearFromString(player, gearstring);
				MessageFunctions.sendTranslatableMessage(source, "collective.deathbackup.message.successfullyloadeddeath", ChatFormatting.DARK_GREEN, DateFunctions.ymdhisToReadable(backupfilename));
				return 1;
			})))
		);
	}
}
