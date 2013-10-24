package web.p6.chat;

import java.io.File;

import org.restlet.Component;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

public final class ChatServer extends ServerResource {
	public final String ROOT = "web";

	public static void main(String[] arg) throws Exception {
		Component c = new Component();
		c.getServers().add(Protocol.HTTP, 8080);
		Router server = new Router();

		server.attach("/chat/{user}/messages", ChatHandler.class);
		server.attachDefault(ChatServer.class);

		c.getDefaultHost().attach(server);
		c.start();
	}

	@Get
	public Representation GET() {
		String path = Reference.decode(getReference()
				.getPath());
		String ext = getReference().getExtensions();
		
		File f = new File(ROOT + path);

		if (!f.exists()) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation("File not found", MediaType.TEXT_PLAIN);
		}

		if ("html".equals(ext)) {
			return new FileRepresentation(f, MediaType.TEXT_HTML);
		} else if ("css".equals(ext)) {
			return new FileRepresentation(f, MediaType.TEXT_CSS);
		} else if ("js".equals(ext)) {
			return new FileRepresentation(f, MediaType.TEXT_JAVASCRIPT);
		} else if ("svg".equals(ext)) {
			return new FileRepresentation(f, MediaType.IMAGE_SVG);
		} else if ("png".equals(ext)) {
			return new FileRepresentation(f, MediaType.IMAGE_PNG);
		} else {
			setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
			return new StringRepresentation("Unsupported media type", MediaType.TEXT_PLAIN);
		}
	}
}
