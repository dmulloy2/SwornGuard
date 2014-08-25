/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.types;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import net.dmulloy2.types.LazyLocation;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author t7seven7t
 */
public class JailData implements ConfigurationSerializable {
	private LazyLocation max, min, spawn, exit;
	private @Getter int jailStage;

	public JailData() {
	}

	public JailData(Map<String, Object> args) {
		this();
		if (args.containsKey("min"))
			min = (LazyLocation) args.get("min");
		if (args.containsKey("max"))
			max = (LazyLocation) args.get("max");
		if (args.containsKey("spawn"))
			spawn = (LazyLocation) args.get("spawn");
		if (args.containsKey("exit"))
			exit = (LazyLocation) args.get("exit");
	}

	public void nextJailStage() {
		jailStage++;
		if (jailStage > 4)
			resetJailStage();
	}

	public void resetJailStage() {
		jailStage = 0;
	}

	public void setMin(Location location) {
		this.min = new LazyLocation(location);
		this.max = null;
	}

	public void setMax(Location location) {
		LazyLocation max = new LazyLocation(location);
		this.max = LazyLocation.getMaximum(max, min);
		this.min = LazyLocation.getMinimum(max, min);
	}

	public Location getSpawn() {
		return spawn.getLocation();
	}

	public void setSpawn(Location loc) {
		spawn = new LazyLocation(loc);
	}

	public Location getExit() {
		return exit.getLocation();
	}

	public void setExit(Location loc) {
		exit = new LazyLocation(loc);
	}

	public boolean isSetup() {
		return ! (max == null || min == null || spawn == null || exit == null || max.equals(min));
	}

	public boolean isInside(Location loc) {
		if (! isSetup())
			return false;

		if (loc.getWorld().getUID().equals(max.getWorld().getUID())) {
			int locX = loc.getBlockX();
			if (locX <= max.getX() && locX >= min.getX()) {
				int locY = loc.getBlockY();
				if (locY <= max.getY() && locY >= min.getY()) {
					int locZ = loc.getBlockZ();
					return locZ <= max.getZ() && locZ >= min.getZ();
				}
			}
		}

		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> args = new LinkedHashMap<>();
		args.put("max", max);
		args.put("min", min);
		args.put("spawn", spawn);
		args.put("exit", exit);
		return args;
	}
}