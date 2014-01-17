package com.lightniinja.snowballwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class GameTick {
	private SBWPlugin pl;
	public GameTick(SBWPlugin pl) {
		this.pl = pl;
	}
	public void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
			@Override
			public void run() {
				for(Arena a: ArenaManager.getManager().getArenas()) {
					if(a.getState() == 1) {
						for(String s:a.getPlayers()) {
							Objective o = a.getSBApi().getObjective("gameTimer", a.getSBApi().getBoard("arena" + a.getId()));
							Score s2 = o.getScore(Bukkit.getOfflinePlayer(s));
							s2.setScore(a.getLives(s));
						}
						if(a.getPlayers().size() == 1) {
							a.gameOver = true;
							Bukkit.broadcastMessage(ChatColor.GREEN + a.getPlayers().get(0) + " has won!");
							a.getUsedSpawns().clear();
							a.setTime(30);
							a.resetState();
							ArenaManager.getManager().removePlayer(Bukkit.getPlayerExact(a.getPlayers().get(0)));
							a.getSBApi().getObjective("gameTimer", a.getSBApi().getBoard("arena" + a.getId())).unregister();
						}
					}
					if(a.getPlayers().size() < 2 && a.getState() == 1) {
						a.gameOver = false;
					}
				}
				
			}
		}, 0L, 20L);
	}
}
