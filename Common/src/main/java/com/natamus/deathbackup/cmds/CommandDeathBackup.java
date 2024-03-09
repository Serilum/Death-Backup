package com.natamus.deathbackup.cmds;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class CommandDeathBackup {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("deathbackup").requires((iCommandSender) -> iCommandSender.hasPermission(2))
			.executes((command) -> {
				CommandSourceStack source = command.getSource();
				MessageFunctions.sendMessage(source, "Death Backup usage:", ChatFormatting.DARK_GREEN);
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
					MessageFunctions.sendMessage(source, "This command can only be executed as a player in-game.", ChatFormatting.RED);
					return 1;
				}
				
				Level world = player.getCommandSenderWorld();
				if (world.isClientSide) {
					MessageFunctions.sendMessage(source, "[Error] The world is not remote, unable to load death backup.", ChatFormatting.RED);
					return 1;
				}
				
				if (!(world instanceof ServerLevel)) {
					MessageFunctions.sendMessage(source, "[Error] Cannot find the world's server, unable to load death backup.", ChatFormatting.RED);
					return 1;
				}
				
				String playername = player.getName().getString().toLowerCase();
				ServerLevel serverworld = (ServerLevel)world;
				
				List<String> backups = Util.getListOfBackups(serverworld, playername);

				MessageFunctions.sendMessage(source, "Last Death Backups: (<index>: <date>)", ChatFormatting.DARK_GREEN, true);
				
				int index = 0;
				for (String ymdhis : backups) {
					MessageFunctions.sendMessage(source, " " + index + ": " + DateFunctions.ymdhisToReadable(ymdhis), ChatFormatting.DARK_GREEN);
					index += 1;
					if (index == 10) {
						break;
					}
				}
				
				MessageFunctions.sendMessage(source, "Load the backup with '/deathbackup load <index>'.", ChatFormatting.YELLOW);
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
					MessageFunctions.sendMessage(source, "This command can only be executed as a player in-game.", ChatFormatting.RED);
					return 1;
				}
				
				Level world = player.getCommandSenderWorld();
				if (world.isClientSide) {
					MessageFunctions.sendMessage(source, "[Error] The world is not remote, unable to load death backup.", ChatFormatting.RED);
					return 1;
				}
				
				if (!(world instanceof ServerLevel)) {
					MessageFunctions.sendMessage(source, "[Error] Cannot find the world's server, unable to load death backup.", ChatFormatting.RED);
					return 1;
				}
				
				String playername = player.getName().getString().toLowerCase();
				ServerLevel serverworld = (ServerLevel)world;
				
				List<String> backups = Util.getListOfBackups(serverworld, playername);
				
				int amount = IntegerArgumentType.getInteger(command, "backup_index");
				if (amount < 0 || amount >= backups.size()) {
					MessageFunctions.sendMessage(source, "The index " + amount + " is invalid.", ChatFormatting.RED);
					return 0;
				}
				
				String backupfilename = backups.get(amount);
				String gearstring = Util.getGearStringFromFile(serverworld, playername, backupfilename);
				if (gearstring.equals("")) {
					MessageFunctions.sendMessage(source, "[Error] Unable to read the backup file.", ChatFormatting.RED);
					return 0;
				}

				PlayerFunctions.setPlayerGearFromString(player, gearstring);
				MessageFunctions.sendMessage(source, "Successfully loaded the death backup from " + DateFunctions.ymdhisToReadable(backupfilename) + " into your inventory.", ChatFormatting.DARK_GREEN);
				return 1;
			})))
		);
	}
}
