package com.lightniinja.snowballwars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SBWPlugin extends JavaPlugin {
	public static int lives = 1;
	public static int snowballs = 1;
	public static boolean rewards;
	public static int rewardType;
	public static int money;
	public static List<ItemStack> items;
	public ArrayList<String> isRun = new ArrayList<String>();
	public static Economy economy;
	public static FileConfiguration cfg;
	@SuppressWarnings("rawtypes")
	private boolean setupEconomy() {
		if(getServer().getPluginManager().isPluginEnabled("Vault")) {
			RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			if(economyProvider != null) {
				economy = (Economy)economyProvider.getProvider();
			}
			return economy != null;
		}
		return false;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onEnable() {
		// save config
		this.saveDefaultConfig();
		cfg = this.getConfig();
		if(this.getConfig().getBoolean("usedb") == true) {
			DatabaseMan.host = this.getConfig().getString("host");
			DatabaseMan.port = this.getConfig().getInt("port");
			DatabaseMan.user = this.getConfig().getString("user");
			DatabaseMan.pass = this.getConfig().getString("pass");
			DatabaseMan.db = this.getConfig().getString("name");
			DatabaseMan.connect();
			new DatabaseMan().update("CREATE TABLE IF NOT EXISTS `scores` (" +
  "`id` int(11) NOT NULL AUTO_INCREMENT," +
  "`username` varchar(32) NOT NULL," +
  "`wins` int(11) NOT NULL," +
  "`losses` int(11) NOT NULL," +
  "`kills` int(11) NOT NULL, " +
  "`deaths` int(11) NOT NULL, " +
  "PRIMARY KEY (`id`)" +
") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
		}
		// register listener
		this.getServer().getPluginManager().registerEvents(new SBWListener(this), this);
		
		// start lobbytimer.
		new LobbyTimer(this).start();
		new GameTick(this).start();
		this.getCommand("swadmin").setExecutor(new CommandAdmin(this));
		this.getCommand("sw").setExecutor(new Command());
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
		snowballs = this.getConfig().getInt("snowballs");
		rewards = this.getConfig().getBoolean("rewards");
		rewardType = this.getConfig().getInt("rewardType");
		money = this.getConfig().getInt("money");
		items = (List)this.getConfig().get("items");
		if (!setupEconomy()) {
		      getLogger().info("Can't use rewardType 0 (money) because economy isn't working (vault not found, or economy not found)! Setting to rewardType 1 automatically!");
		      rewardType = 1;
		      return;
		}
		
		
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
