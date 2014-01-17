package com.lightniinja.snowballwars;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdmin implements CommandExecutor {
	private SBWPlugin pl = null;
	private HashMap<String, List<Location>> spawns = new HashMap<String, List<Location>>();
	public CommandAdmin(SBWPlugin pl) {
		this.pl = pl;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Be player to use command");
			return true;
		}
		Player p = (Player)sender;
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("help")) {
				p.sendMessage("/swadmin create - create arena");
				p.sendMessage("/swadmin addspawn - add spawn");
				p.sendMessage("/swadmin save - save arena to file");
				p.sendMessage("/swadmin cancel - clear current spawns");
				p.sendMessage("/swadmin remove id - remove arena id [/arenas/arena%id%.yml]");
				p.sendMessage("/swadmin help - show help");
			} else if(args[0].equalsIgnoreCase("create")) {
				p.sendMessage(ChatColor.GREEN + "/swadmin addspawn for spawns, /swadmin save");
			} else if(args[0].equalsIgnoreCase("addspawn")) {
				if(spawns.containsKey(p.getName())) {
					List<Location> spw = spawns.get(p.getName());
					spw.add(p.getLocation());
					spawns.remove(p.getName());
					spawns.put(p.getName(), spw);
					p.sendMessage(ChatColor.GREEN + "Added spawn.");
				} else {
					List<Location> spw = new ArrayList<Location>();
					spw.add(p.getLocation());
					spawns.put(p.getName(), spw);
					p.sendMessage(ChatColor.GREEN + "Added spawn.");
				}
			} else if(args[0].equalsIgnoreCase("save")) {
				this.pl.saveToFile(ArenaManager.getManager().createArena(spawns.get(p.getName())));
				p.sendMessage(ChatColor.GREEN + "Saved arena, do a /reload");
			} else if(args[0].equalsIgnoreCase("cancel")) {
				spawns.remove(p.getName());
				p.sendMessage(ChatColor.GREEN + "Cleared spawns");
			} else {
				sender.sendMessage(ChatColor.RED + "Command not found!");
				return false;
			}
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("remove")) {
				File f = new File(this.pl.getDataFolder() + "/arenas/arena" + args[1] + ".yml");
				f.delete();
				p.sendMessage(ChatColor.GREEN + "Arena removed!");
			} else {
				sender.sendMessage(ChatColor.RED + "Command not found!");
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Command not found!");
			return false;
		}
		return true;
	}
}
