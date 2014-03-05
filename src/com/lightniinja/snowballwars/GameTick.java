package com.lightniinja.snowballwars;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
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
							Bukkit.getPlayerExact(s).setGameMode(GameMode.ADVENTURE);
						}
						if(a.getPlayers().size() == 1) {
							a.gameOver = true;
							Bukkit.broadcastMessage(ChatColor.GREEN + a.getPlayers().get(0) + " has won!");
							a.clearVotes();
							a.getUsedSpawns().clear();
							a.setTime(30);
							a.resetState();
							ArenaManager.getManager().removePlayer(Bukkit.getPlayerExact(a.getPlayers().get(0)));
							if(SBWPlugin.rewards == true) {
								if(SBWPlugin.rewardType == 0) {
									EconomyResponse r = SBWPlugin.economy.depositPlayer(a.getPlayers().get(0), SBWPlugin.money);
									if(r.transactionSuccess())
										Bukkit.getPlayerExact(a.getPlayers().get(0)).sendMessage(ChatColor.GREEN + "$" + SBWPlugin.money + " has been added to your account for winning!");
								} else {
									for(ItemStack is: SBWPlugin.items) {
										Bukkit.getPlayerExact(a.getPlayers().get(0)).getInventory().addItem(new ItemStack[] { is });
									}
								}
							}
								if(SBWPlugin.cfg.getBoolean("usedb")) {
									new DatabaseMan().update("UPDATE `scores` SET `wins`=`wins`+1 WHERE `username`='" + a.getPlayers().get(0) +"'");
								}
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
