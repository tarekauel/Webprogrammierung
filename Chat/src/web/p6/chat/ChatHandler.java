package web.p6.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

public class ChatHandler extends ServerResource {
	private static Map<String, User> users = new HashMap<String, User>();
	private static ReadWriteLock lock = new ReentrantReadWriteLock();

	private Gson gson = new Gson();

	private void addMessage(Message message) {
		try {
			lock.writeLock().lock();

			// ggf. werden die Benutzer noch angelegt
			User fromUser = users.get(message.getFromUser());
			if (fromUser == null) {
				fromUser = new User();
				users.put(message.getFromUser(), fromUser);
			}
			User toUser = users.get(message.getToUser());
			if (toUser == null) {
				toUser = new User();
				users.put(message.getToUser(), toUser);
			}

			fromUser.addMessage(message);
			toUser.addMessage(message);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Get
	public Representation GET() {
		String userStr = (String) this.getRequest().getAttributes().get("user");

		String resultJson = null;
		try {
			lock.readLock().lock();

			User user = users.get(userStr);
			if (user == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return new StringRepresentation("User does not exist",
						MediaType.TEXT_PLAIN);
			}
			resultJson = gson.toJson(user.getMessages());
		} finally {
			lock.readLock().unlock();
		}

		return new StringRepresentation(resultJson, MediaType.APPLICATION_JSON);
	}

	@Post
	public Representation POST(Representation entity) throws IOException {
		// Alternativ: Form Data
		
		 Form form = new Form(entity);
		 String fromUserStr = form.getFirstValue("fromUser");
		 String toUserStr = form.getFirstValue("toUser");
		 String text = form.getFirstValue("text");
		 Message message = new Message(fromUserStr, toUserStr, text);

		// TODO besseres Fehlerhandling
		//String content = entity.getText();
		//Message message = gson.fromJson(content, Message.class);
		System.out.println("adding message " + message);

		addMessage(message);

		String resultJson = gson.toJson(message);
		return new StringRepresentation(resultJson, MediaType.APPLICATION_JSON);
	}
}