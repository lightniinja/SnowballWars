package com.lightniinja.snowballwars;

//import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
//import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class ArenaManager {

	private static SBWPlugin pl = null;
	private static ArenaManager a = new ArenaManager(pl);
	private List<Arena> arenas = new ArrayList<Arena>();
	private int arenaSize = 0;
	private HashMap<String, ItemStack[]> inventory = new HashMap<String, ItemStack[]>();
	private HashMap<String, ItemStack[]> armor = new HashMap<String, ItemStack[]>();
	private HashMap<String, Location> locs = new HashMap<String, Location>();
	
	public ArenaManager(SBWPlugin pl) {
		ArenaManager.pl = pl;
	}
	
	public static ArenaManager getManager() {
		return a;
	}
	
	public Arena getArena(int i) {
		for(Arena a: arenas) {
			if(a.getId() == i) {
				return a;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public void addPlayer(Player p, int i) {
		Arena a = getArena(i);
		if(a == null) {
			p.sendMessage(ChatColor.RED + "Arena not found! (You shouldn't be seeing this!)");
			return;
		}
		if(a.getPlayers().size() > 8) {
			p.sendMessage(ChatColor.RED + "Arena is full! Try joining a different one!");
			return;
		}
		if(a.getState() != 0) {
			p.sendMessage(ChatColor.RED + "Arena has already started!");
			return;
		}
		if(a.getPlayers().contains(p.getName())) {
			return;
		}
		p.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + p.getName() + " has joined the arena!");
		for(String str: a.getPlayers()) {
			Bukkit.getPlayerExact(str).sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + p.getName() + " has joined the arena!");
		}
		inventory.put(p.getName(), p.getInventory().getContents());
		armor.put(p.getName(), p.getInventory().getArmorContents());
		locs.put(p.getName(), p.getLocation());
		p.getInventory().clear();
		p.updateInventory();
		a.getPlayers().add(p.getName());
		//int i2 = (YamlConfiguration.loadConfiguration(new File("config.yml"))).getInt("lives");
		a.addLives(p.getName(), SBWPlugin.lives);
		p.teleport(a.getNextSpawn());
		a.getUsedSpawns().put(p.getName(), a.getNextSpawn());
		p.getInventory().addItem(new ItemStack(Material.SNOW_BALL, SBWPlugin.snowballs));
		ItemStack item = new ItemStack(Material.PAPER, 1);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(ChatColor.YELLOW + "Leave Arena");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "Right click to leave arena!");
		item.setItemMeta(m);
		p.getInventory().setItem(8, item);
		p.setScoreboard(a.getSBApi().getBoard("arena" + a.getId()));
	}
	
	@SuppressWarnings("deprecation")
	public void removePlayer(Player p) {
		if(getArena(p) == null)
			return;
		if(getArena(p).getState() == 1) {	
			Objective o = getArena(p).getSBApi().getObjective("gameTimer", getArena(p).getSBApi().getBoard("arena" + getArena(p).getId()));
			Score s = o.getScore(Bukkit.getOfflinePlayer(p.getName()));
			s.setScore(-1);
		}
		Arena a = getArena(p);
		a.getUsedSpawns().remove(p.getName());
		a.getPlayers().remove(p.getName());
		a.removeLives(p.getName());
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		p.getInventory().clear();
		p.updateInventory();
		p.getInventory().setContents(inventory.get(p.getName()));
		inventory.remove(p.getName());
		p.getInventory().setArmorContents(armor.get(p.getName()));
		armor.remove(p.getName());
		p.teleport(locs.get(p.getName()));
		locs.remove(p.getName());
		p.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + p.getName() + " has left the arena!");
		for(String str: a.getPlayers()) {
			Bukkit.getPlayerExact(str).sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + p.getName() + " has left the arena!");
		}
	}

	public List<Arena> getArenas() {
		return this.arenas;
	}
	
	public Arena createArena(List<Location> list){
        int num = arenaSize + 1;
        arenaSize++;
        Arena a = new Arena(list, num);
        arenas.add(a);
 
        return a;
    }
 
	public Arena getArena(Player p) {
		for(Arena a: arenas) {
			if(a.getPlayers().contains(p.getName()))
				return a;
		}
		return null;
	}
	
    public boolean isInGame(Player p){
        for(Arena a : arenas){
            if(a.getPlayers().contains(p.getName()))
                return true;
        }
        return false;
    }
 
    public String serializeLoc(Location l){
        return l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
    }
    public Location deserializeLoc(String s){
        String[] st = s.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
    }
    
   
	
}
