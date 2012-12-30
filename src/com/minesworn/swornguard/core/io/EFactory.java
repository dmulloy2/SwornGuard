package com.minesworn.swornguard.core.io;

public interface EFactory<E extends Entity> {

	public abstract E newEntity();
	
}
