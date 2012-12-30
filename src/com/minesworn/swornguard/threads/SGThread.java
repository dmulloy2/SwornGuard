package com.minesworn.swornguard.threads;

public abstract class SGThread implements Runnable {
	Thread t;
	public SGThread() {
		t = new Thread(this);
		t.start();
	}
}
