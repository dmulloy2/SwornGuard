/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.types;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

/**
 * @author t7seven7t
 */
public class JailData implements ConfigurationSerializable {
	private @Getter int jailStage;
	private SimpleVector max, min, spawn, exit;
	private int spawnyaw, exityaw;
	private @Getter @Setter World world;
	
	public JailData() {
		world = Bukkit.getWorlds().get(0);
	}
	
	public JailData(Map<String, Object> args) {
		this();
		if (args.containsKey("min"))
			min = (SimpleVector) args.get("min");
		if (args.containsKey("max"))
			max = (SimpleVector) args.get("max");
		if (args.containsKey("spawn"))
			spawn = (SimpleVector) args.get("spawn");
		if (args.containsKey("exit"))
			exit = (SimpleVector) args.get("exit");
		if (args.containsKey("spawnyaw"))
			spawnyaw = (int) args.get("spawnyaw");
		if (args.containsKey("exityaw"))
			exityaw = (int) args.get("exityaw");
		if (args.containsKey("world"))
			world = Bukkit.getWorld((String) args.get("world"));
	}
	
	public void nextJailStage() {
		jailStage++;
		
		if (jailStage > 4)
			resetJailStage();
	}
	
	public void resetJailStage() {
		jailStage = 0;
	}
	
	public void setMin(Vector min) {
		this.min  = new SimpleVector(min); 
		this.max = null;
	}
	
	public void setMax(Vector v1) {
		Vector v2 = this.min.toVector();
		
		this.min = new SimpleVector(Vector.getMinimum(v1, v2));
		this.max = new SimpleVector(Vector.getMaximum(v1, v2));
	}
	
	public Location getSpawn() {
		return simpleVectorToLocation(spawn, spawnyaw);
	}
	
	public void setSpawn(Location l) {
		spawn = new SimpleVector(l);
		spawnyaw = (int) l.getYaw();
	}
	
	public Location getExit() {
		return simpleVectorToLocation(exit, exityaw);
	}
	
	public void setExit(Location l) {
		exit = new SimpleVector(l);
		exityaw = (int) l.getYaw();
	}
	
	private Location simpleVectorToLocation(SimpleVector v, int yaw) {
		return new Location(world, v.x, v.y, v.z, yaw, 0F);
	}
	
	public boolean isSetup() {
		return !(max == null || min == null || spawn == null || exit == null || world == null || max.equals(min));
	}
	
	public boolean isInside(Location l) {
		if (!isSetup())
			return false;
		SimpleVector v = new SimpleVector(l);
		return !(min.x > v.x || min.y > v.y || min.z > v.z || max.x < v.x || max.y < v.y || max.z < v.z);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("max", max);
		args.put("min", min);
		args.put("spawn", spawn);
		args.put("exit", exit);
		args.put("spawnyaw", spawnyaw);
		args.put("exityaw", exityaw);
		args.put("world", world.getName());
		return args;
	}

}
