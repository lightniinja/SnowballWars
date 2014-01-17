package com.lightniinja.snowballwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyTimer {
	private SBWPlugin pl;
	public LobbyTimer(SBWPlugin pl) {
		this.pl = pl;
	}
	public void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
			@Override
			public void run() {
				for(Arena a: ArenaManager.getManager().getArenas()) {
					if(a.getTime() == 0 && a.getState() == 0) {
						a.nextState();
						Scoreboard s = a.getSBApi().getBoard("arena" + a.getId());
						a.getSBApi().getObjective("lobbyTimer", s).unregister();
						Objective o = a.getSBApi().getObjective("gameTimer", s);
						o.setDisplayName(ChatColor.GOLD + "Players:");
						for(String str:a.getPlayers()) {
							Score sc = o.getScore(Bukkit.getOfflinePlayer(str));
							sc.setScore(1);
						}
					} else {
						if(a.getPlayers().size() >= (Math.round(a.getSpawn().size() / 2)) && a.getPlayers().size() >= 2 && a.getState() == 0) {
							a.setTime(a.getTime() - 1);
							Objective o = a.getSBApi().getObjective("lobbyTimer", a.getSBApi().getBoard("arena" + a.getId()));
							o.setDisplayName(ChatColor.YELLOW + "Waiting...");
							o.setDisplaySlot(DisplaySlot.SIDEBAR);
							Score s = o.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Starting in: "));
							s.setScore(a.getTime());
							s = o.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Players:"));
							s.setScore(a.getPlayers().size());
						}
					}
					if(a.getState() == 0) {
						for(String s: a.getPlayers()) {
							if(a.getUsedSpawns().get(s).distance(Bukkit.getPlayerExact(s).getLocation()) > 1) {
								Bukkit.getPlayerExact(s).teleport(a.getUsedSpawns().get(s));
							}
						}
					}
				}
			}
		}, 0L, 20L);
		
	}
}
