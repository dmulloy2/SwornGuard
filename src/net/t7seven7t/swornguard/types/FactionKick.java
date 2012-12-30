/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.types;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

/**
 * @author t7seven7t
 */
public class FactionKick {
	private final String faction;
	private final long time;
	
	public FactionKick(final String faction, final long time) {
		this.faction = faction;
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}
	
	public Faction getFaction() {
		return Factions.i.getByTag(faction);
	}
}
