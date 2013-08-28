package tcpsuche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MessageContainer implements Serializable {
	
	private ArrayList<String> message = new ArrayList<String>();
	private Date date;
	
	public MessageContainer( ArrayList<String> message) {
		this.message = message;
		this.date = new Date();
	}
	
	public ArrayList<String> getMessage() {
		return message;
	}
	
	public Date getDate() {
		return date;
	}
	

}
