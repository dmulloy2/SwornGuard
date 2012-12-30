package com.minesworn.swornguard.core;

import com.minesworn.swornguard.core.util.SThread;

public class ReloadThread extends SThread {
	@Override
	public void run() {
		try {
			SPlugin.p.onDisable();
			Thread.sleep(2000);
			SPlugin.p.onEnable();
			SPlugin.p.afterReload();
		} catch (InterruptedException e) {}
	}

}
