package restletchat;

import java.util.HashMap;

public class User {
	
	private final String username;
	
	private final static HashMap<String, User> userList = new HashMap<String, User>();
	
	public User( String name) {
		this.username = name;
		if( userList.get(name) != null ) {
			throw new IllegalArgumentException("Username " + username + " existiert bereits!");
		}	
		userList.put(name, this);
		System.out.println("User: " + username + " wurde angelegt!");
	}
	
	@Override
	public String toString() {
		return "User: " + username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
