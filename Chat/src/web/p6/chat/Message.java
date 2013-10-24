package web.p6.chat;


public class Message {	
		public Message(String fromUser, String toUser, String text) {
			this.toUser = toUser;
			this.fromUser = fromUser;
			this.text = text;
		}

		private final String fromUser;
		private final String toUser;
		private final String text;

		public String getFromUser() {
			return fromUser;
		}

		public String getToUser() {
			return toUser;
		}
		
		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return "Message [fromUser=" + fromUser + ", toUser=" + toUser
					+ ", text=" + text + "]";
		}
	}