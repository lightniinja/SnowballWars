package com.lightniinja.snowballwars;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

	private int id = 0;
	private List<Location> spawns = new ArrayList<Location>();
	private List<String> players = new ArrayList<String>();
	private HashMap<String, Location> usedSpawns = new HashMap<String, Location>();
	private int time = 30;
	private int state = 0;
	private HashMap<String, Integer> lives = new HashMap<String, Integer>();
	private ScoreboardAPI sbapi = new ScoreboardAPI();
	protected boolean gameOver;
	
	public Arena(List<Location> list, int id) {
		this.spawns = list;
		this.id = id;
	}
	
	public int getTime() {
		return this.time;
	}
	public void setTime(int i) {
		this.time = i;
	}
	
	public int getState() {
		return this.state;
	}
	public void nextState() {
		this.state++;
	}
	public void resetState() {
		this.state = 0;
	}
	
	public Integer getLives(String p) {
		if(this.lives.get(p) != null)
			return this.lives.get(p);
		return null;
	}
	public void setLives(String p, int l) {
		this.lives.remove(p);
		this.lives.put(p, l);
	}
	public void addLives(String p, int l) {
		this.lives.put(p, l);
	}
	public int getId() {
		return this.id;
	}
	public HashMap<String, Location> getUsedSpawns() {
		return this.usedSpawns;
	}
	
	public List<String> getPlayers() {
		return this.players;
	}
	
	public void setId(int i) {
		this.id = i;
	}
	public List<Location> getSpawn() {
		return spawns;
	}
	
	public Location getNextSpawn() {
		for(Location l: this.spawns) {
			if(!usedSpawns.containsValue(l)) {
				return l;
			} else {
				continue;
			}
		}
		System.out.print("NO SPAWN?? :(");
		return spawns.get(0);
	}

	public ScoreboardAPI getSBApi() {
		return this.sbapi;
	}

	public void removeLives(String name) {
		this.lives.remove(name);
	}
}
