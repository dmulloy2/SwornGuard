package com.minesworn.swornguard.detectors.chatmanager;

public class Message {

	private String message;
	private long timestamp;
	
	public Message(String msg, long now) {
		message = msg;
		timestamp = now;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getMessage() {
		return message;
	}
	
}
