package web.p6.chat;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class User {
		private List<Message> messageList = new LinkedList<Message>();

		public void addMessage(Message message) {
			messageList.add(message);
		}

		public List<Message> getMessages() {
			return Collections.unmodifiableList(messageList);
		}

		@Override
		public String toString() {
			return "User [messageList=" + messageList + "]";
		}
	}