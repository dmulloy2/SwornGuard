package com.minesworn.swornguard.detectors.factionbetrayal;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class Kick {
	 String faction;
	 long time;
	 
	 public Kick(String f, long t) {
	  this.faction = f;
	  this.time = t;
	 }	
	 
	public long getTime() {
		return time;
	}

	public Faction getFaction() {
		 return Factions.i.getByTag(faction);
	}
	
}
