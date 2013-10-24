package restletchat;

import java.util.ArrayList;

public class Conversation {
	
	private final ArrayList<String> messages = new ArrayList<String>();
	private final User userA;
	private final User userB;
	
	public Conversation(User a, User b) {
		this.userA = a;
		this.userB = b;
	}

	public User getUserA() {
		return userA;
	}

	public User getUserB() {
		return userB;
	}

	public ArrayList<String> getMessages() {
		return messages;
	}
	
	public void addMessage(String m) {
		messages.add(m);
	}

}
