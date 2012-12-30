package com.minesworn.swornguard.detectors.chatmanager;

import java.util.HashSet;

public class Messages {

	private HashSet<Message> messages = new HashSet<Message>();
	
	public void addMessage(String msg, long now) {	
		messages.add(new Message(msg, now));
	}
	
	public void removeMessage(Message message) {
		messages.remove(message);
	}
	
	public Message[] getMessages() {
		return messages.toArray(new Message[0]);
	}
	
}
