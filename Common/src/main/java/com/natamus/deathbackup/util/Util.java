package com.natamus.deathbackup.util;

import com.natamus.collective.functions.WorldFunctions;
import net.minecraft.server.level.ServerLevel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
	public static void writeGearStringToFile(ServerLevel serverworld, String playername, String filename, String gearstring) {
		String deathbackuppath = WorldFunctions.getWorldPath(serverworld) + File.separator + "data" + File.separator + "deathbackup" + File.separator + playername;
		File dir = new File(deathbackuppath);
		dir.mkdirs();
		
		try {
			PrintWriter writer = new PrintWriter(deathbackuppath + File.separator + filename + ".txt", StandardCharsets.UTF_8);
			writer.println(gearstring);
			writer.close();
		} catch (Exception ignored) { }
	}
	
	public static String getGearStringFromFile(ServerLevel serverworld, String playername, String filename) {
		String deathbackuppath = WorldFunctions.getWorldPath(serverworld) + File.separator + "data" + File.separator + "deathbackup" + File.separator + playername;
		File dir = new File(deathbackuppath);
		File file = new File(deathbackuppath + File.separator + filename + ".txt");
		
		String gearstring = "";
		if (dir.isDirectory() && file.isFile()) {
			try {
				gearstring = new String(Files.readAllBytes(Paths.get(deathbackuppath + File.separator + filename + ".txt", new String[0])));
			} catch (IOException ignored) { }
		}
		
		return gearstring;
	}
	
	public static List<String> getListOfBackups(ServerLevel serverworld, String playername) {
		List<String> backups = new ArrayList<String>();
		
		String deathbackuppath = WorldFunctions.getWorldPath(serverworld) + File.separator + "data" + File.separator + "deathbackup" + File.separator + playername;
		File folder = new File(deathbackuppath);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null) {
			return backups;
		}

		for (File listOfFile : listOfFiles) {
			if (listOfFile.isFile()) {
				backups.add(listOfFile.getName().replace(".txt", ""));
			}
		}
		
		Collections.reverse(backups);
		return backups;
	}
}