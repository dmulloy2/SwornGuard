package com.minesworn.swornguard;

import com.minesworn.swornguard.core.io.EFactory;

public class PlayerInfoFactory implements EFactory<PlayerInfo> {

	@Override
	public PlayerInfo newEntity() {
		return new PlayerInfo();
	}

}
