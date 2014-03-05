package com.lightniinja.snowballwars;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SBWListener implements Listener {
	private SBWPlugin pl = null;
	public SBWListener(SBWPlugin pl) {
		this.pl = pl;
	}
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		e.setCancelled(ArenaManager.getManager().isInGame(e.getPlayer()));
	}
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		e.setCancelled(ArenaManager.getManager().isInGame(e.getPlayer()));
	}
	@EventHandler
	public void onPlayerSnowballHit(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
			Snowball s = (Snowball)e.getDamager();
			Player p = (Player)e.getEntity();
			if(ArenaManager.getManager().getArena(p).getState() != 1) 
				return;
			if(s.getShooter() instanceof Player) {
				p.sendMessage(ChatColor.RED + "Snowballed by " + ((Player)s.getShooter()).getName() + "!");
				((Player)s.getShooter()).sendMessage(ChatColor.RED + "Snowballed " + p.getName() + "!");
				((Player)s.getShooter()).playSound(s.getShooter().getLocation(), Sound.ENDERDRAGON_HIT, 1.0F, 1.0F);
				if(ArenaManager.getManager().getArena(p).getLives(p.getName()) == 1) {
					ArenaManager.getManager().removePlayer(p);
					if(SBWPlugin.cfg.getBoolean("usedb")) {
						new DatabaseMan().update("UPDATE `scores` SET `deaths`=`deaths`+1 WHERE `username`='" + p.getName() +"'");
						new DatabaseMan().update("UPDATE `scores` SET `losses`=`losses`+1 WHERE `username`='" + p.getName() +"'");
						new DatabaseMan().update("UPDATE `scores` SET `kills`=`kills`+1 WHERE `username`='" + ((Player)s.getShooter()).getName() +"'");
					}
				} else {
					ArenaManager.getManager().getArena(p).setLives(p.getName(), ArenaManager.getManager().getArena(p).getLives(p.getName()) - 1);
					p.teleport(ArenaManager.getManager().getArena(p).getUsedSpawns().get(p.getName()));
					if(SBWPlugin.cfg.getBoolean("usedb")) {

						new DatabaseMan().update("UPDATE `scores` SET `deaths`=`deaths`+1 WHERE `username`='" + p.getName() +"'");
						new DatabaseMan().update("UPDATE `scores` SET `kills`=`kills`+1 WHERE `username`='" + ((Player)s.getShooter()).getName() +"'");
					}
				}
			}
		} else {
			if(e.getEntity().getType() == EntityType.PLAYER) {
				Player p =  (Player)e.getEntity();
				if(ArenaManager.getManager().getArena(p) != null) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player)e.getEntity();
		if(ArenaManager.getManager().getArena(p) == null) {
			return;
		}
		if(e.getCause() != DamageCause.PROJECTILE) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onFoodDecrease(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player)e.getEntity();
		if(ArenaManager.getManager().getArena(p) != null) {
			e.setCancelled(true);
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLeavePaper(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getItem() != null) {
				if(e.getItem().getType() == Material.PAPER) {
					ArenaManager.getManager().removePlayer(e.getPlayer());
				} else if(e.getItem().getType() == Material.SNOW_BALL) {
					if(ArenaManager.getManager().getArena(e.getPlayer()) != null) {
						if(ArenaManager.getManager().getArena(e.getPlayer()).getState() != 1) {
							e.setCancelled(true);
							e.getPlayer().updateInventory();
						}
					}
				}
			}
		}
	}
	public ArrayList<String> getArenas() {
		File folder = new File(this.pl.getDataFolder() + "/arenas");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> s = new ArrayList<String>();
		for(File f: listOfFiles) {
			s.add(f.getName());
		}
		return s;
	}
	@EventHandler
	public void onSignEdit(SignChangeEvent e) {
		if(!e.getPlayer().hasPermission("snowballwars.createsign")) 
			return;
		if(e.getLine(0).equalsIgnoreCase("[Snowball]")) {
			e.setLine(0, ChatColor.BLUE + "[Snowball]");
		}
	}
	@EventHandler
	public void onSignRightClick(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if(s.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[Snowball]")) {
					Double d = (double) ArenaManager.getManager().getArenas().size();
					d = d / 9;
					d = Math.ceil(d);
					if(d == 0)
						d++;
					d = d * 9;
					 IconMenu menu = new IconMenu(ChatColor.RED + "Matches", d.intValue(), new IconMenu.OptionClickEventHandler() {
				            @Override
				            public void onOptionClick(IconMenu.OptionClickEvent event) {
				                if(event.getName().equalsIgnoreCase("No arenas!")) {
				                	event.setWillClose(true);
				                	return;
				                }
				            	ArenaManager.getManager().addPlayer(event.getPlayer(), Integer.valueOf(event.getName()));
				                event.setWillClose(true);
				            }
				        }, this.pl);
				        //.setOption(3, new ItemStack(Material.APPLE, 1), "Food", "The food is delicious")
					 	if(ArenaManager.getManager().getArenas().size() == 0) {
					 		menu.setOption(0, new ItemStack(Material.BEDROCK, 1), "No arenas!", "This server hasn't setup any arenas yet!");
					 	}
				        for(int i2 = 0; i2 < ArenaManager.getManager().getArenas().size(); i2++) {
				        	menu.setOption(i2, new ItemStack(Material.BEDROCK, 1), "" + ArenaManager.getManager().getArenas().get(i2).getId(), "Click to join.");
				        }
				        menu.open(e.getPlayer());
				}
			}
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		ArenaManager.getManager().removePlayer(e.getPlayer());
	}
	@EventHandler
	public void onLeave2(PlayerKickEvent e) {
		ArenaManager.getManager().removePlayer(e.getPlayer());
	}
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(ArenaManager.getManager().getArena(e.getPlayer()) != null) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
			if(SBWPlugin.cfg.getBoolean("usedb")) {
				new DatabaseMan().update("INSERT INTO `scores` (`id`, `username`, `wins`, `losses`, `kills`, `deaths`) VALUES (NULL, '" + e.getPlayer().getName() + "', '0', '0', '0', '0');");
			}
	}
	@EventHandler
	public void onEggLand(ProjectileHitEvent e) {
		if(e.getEntity().getShooter() instanceof Player) {
			Player s = (Player)e.getEntity().getShooter();
			if(ArenaManager.getManager().getArena(s) != null) {
				FireworkEffectPlayer fplayer = new FireworkEffectPlayer();
				try {
					fplayer.playFirework(e.getEntity().getWorld(), e.getEntity().getLocation(), getRandom());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock() != null) {
				if(e.getClickedBlock().getType() == Material.SNOW) {
					if(this.pl.isRun.contains(e.getPlayer().getName())) 
						return;
					this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable() {
						final Player p = e.getPlayer();
						final SBWPlugin pl2 = pl;
						@Override
						public void run() {
							if(ArenaManager.getManager().getArena(p) != null) {
								if(ArenaManager.getManager().getArena(p).getState() == 1) { 
									p.setExp(0F);
									p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
									p.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1));
									pl2.isRun.remove(p.getName());
								} else {
									this.pl2.isRun.remove(e.getPlayer().getName());
									p.setExp(0F);
								}
							} else {
								this.pl2.isRun.remove(e.getPlayer().getName());
								p.setExp(02);
							} 
						}
					}, 20*3);
					this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable() {
						final Player p = e.getPlayer();
						final SBWPlugin pl2 = pl;
						@Override
						public void run() {
							if(ArenaManager.getManager().isInGame(p)) {
								if(ArenaManager.getManager().getArena(p).getState() == 1) {
								p.setExp(0.35F);
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.9F);
								}else {
									this.pl2.isRun.remove(e.getPlayer().getName());
								}
							}else {
								this.pl2.isRun.remove(e.getPlayer().getName());
							}
						}
					}, 20*2);
					this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable() {
						final Player p = e.getPlayer();
						final SBWPlugin pl2 = pl;
						@Override
						public void run() {
							if(ArenaManager.getManager().isInGame(p)) {
								if(ArenaManager.getManager().getArena(p).getState() == 1) {
								p.setExp(0.55F);
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.8F);
								}else {
									this.pl2.isRun.remove(e.getPlayer().getName());
								}
							}else {
								this.pl2.isRun.remove(e.getPlayer().getName());
							}
						}
					}, 20*1);
					this.pl.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable() {
						final Player p = e.getPlayer();
						final SBWPlugin pl2 = pl;
						@Override
						public void run() {
							if(ArenaManager.getManager().getArena(p) != null) {
								if(ArenaManager.getManager().getArena(p).getState() == 1) {
									p.setExp(1.0F);
									p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.7F);
									this.pl2.isRun.add(e.getPlayer().getName());
								} else {
									this.pl2.isRun.remove(e.getPlayer().getName());
								}
							} else {
								this.pl2.isRun.remove(e.getPlayer().getName());
							}
						}
					}, 1);
				}
			}
		}
	}
	
	public FireworkEffect getRandom() {
		Random r = new Random();
		Type t = null;
		switch(r.nextInt(4)) {
		case 0:
			t = Type.BALL;
			break;
		case 1:
			t = Type.BALL_LARGE;
			break;
		case 2:
			t = Type.BURST;
			break;
		case 3:
			t = Type.CREEPER;
			break;
		case 4:
			t = Type.STAR;
			break;
		}
		// Blue, Orange
		// Yellow, Green
		// Yellow, Purple
		// Fuchisa, Silver
		// Black, White
		Color w1 = null;
		Color w2 = null;
		switch(r.nextInt(4)) {
		case 0:
			w1 = Color.BLUE;
			w2 = Color.ORANGE;
			break;
		case 1:
			w1 = Color.YELLOW;
			w2 = Color.GREEN;
			break;
		case 2:
			w1 = Color.YELLOW;
			w2 = Color.PURPLE;
			break;
		case 3:
			w1 = Color.FUCHSIA;
			w2 = Color.SILVER;
			break;
		case 4:
			w1 = Color.BLACK;
			w2 = Color.WHITE;
			break;
		}
		Color w3 = null;
		Color w4 = null;
		switch(r.nextInt(4)) {
		case 0:
			w3 = Color.BLUE;
			w4 = Color.ORANGE;
			break;
		case 1:
			w3 = Color.YELLOW;
			w4 = Color.GREEN;
			break;
		case 2:
			w3 = Color.YELLOW;
			w4 = Color.PURPLE;
			break;
		case 3:
			w3 = Color.FUCHSIA;
			w4 = Color.SILVER;
			break;
		case 4:
			w3 = Color.BLACK;
			w4 = Color.WHITE;
			break;
		}
 		return FireworkEffect.builder().with(t).withColor(w1).withColor(w2).withFade(w3).withFade(w4).build();
	}
	
	
}
