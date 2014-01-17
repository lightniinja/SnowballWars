package com.lightniinja.snowballwars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SBWPlugin extends JavaPlugin {
	public static int lives = 1;
	public ArrayList<String> isRun = new ArrayList<String>();
	public void onEnable() {
		// save config
		this.saveDefaultConfig();
		
		// register listener
		this.getServer().getPluginManager().registerEvents(new SBWListener(this), this);
		
		// start lobbytimer.
		new LobbyTimer(this).start();
		new GameTick(this).start();
		this.getCommand("swadmin").setExecutor(new CommandAdmin(this));
		File folder = new File(this.getDataFolder() + "/arenas");
		if(!folder.exists()) {
			folder.mkdir();
		}
		File[] listOfFiles = folder.listFiles();
		for(File f: listOfFiles) {
			loadFromFile(f.getName());
			System.out.print(f.getName());
		}
		lives = this.getConfig().getInt("lives");
	}
	public void loadFromFile(String name) {
	    	File f = new File(this.getDataFolder() + "/arenas/" + name);
	    	FileConfiguration c = YamlConfiguration.loadConfiguration(f);
	    	ArrayList<Location> locs = new ArrayList<Location>();
	    	for(String s: c.getStringList("spawns")) {
	    		locs.add(ArenaManager.getManager().deserializeLoc(s));
	    	}
	    	ArenaManager.getManager().createArena(locs);
	}
	public void saveToFile(Arena a) {
		File f = new File(this.getDataFolder() + "/arenas/arena" + a.getId() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		List<String> wee = new ArrayList<String>();
		for(Location l: a.getSpawn()) {
			wee.add(ArenaManager.getManager().serializeLoc(l));
		}
		c.set("spawns", wee);
		try {
			c.save(f);
		} catch (IOException e) {
			// do you even catch bro?
		}
	}
}
