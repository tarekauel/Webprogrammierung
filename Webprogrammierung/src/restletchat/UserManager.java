package restletchat;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class UserManager extends ServerResource {

	
	 /*@Get
	 public Representation get() {
		 String name = (String) this.getRequest().getAttributes().get("user");
		 User u = 
		 return new StringRepresentation("...", MediaType.TEXT_HTML);
	 }*/

	@Post
	public Representation post(Representation entity) {
		String name = (String) this.getRequest().getAttributes().get("user");
		User u;
		try {
			u = new User(name);
		} catch (IllegalArgumentException e) {
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_HTML);
		}
		return new StringRepresentation(u.toString(), MediaType.TEXT_HTML);

	}

}
