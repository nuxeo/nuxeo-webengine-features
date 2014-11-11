package org.nuxeo.ecm.webengine.samples;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

/**
 * User object.
 * You can see the @WebObject annotation that is defining a WebObject of type "User"
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@WebObject(type="User")
@Produces("text/html;charset=UTF-8")
public class UserObject extends DefaultObject {

    String displayName;

    /**
     * Initialize the object.
     * args values are the one passed to the method newObject(String type, Object ... args)
     */
    protected void initialize(Object... args) {
        displayName = (String) args[0];
    }

    /**
     * Getter the variable displayName. Would be accessible from views with ${This.displayName}
     */
    public String getDisplayName() {
        return displayName;
    }


    /**
     * Get the index view of the User object.
     * The view file is located in {@code skin/views/User} so that it can be easily extended
     * by a derived module. See extensibility sample.
     */
    @GET
    public Object doGet() {
        return getView("index");
    }

    /**
     * This method is not implemented but demonstrates how DELETE requests can be used
     */
    @DELETE
    public Object doRemove(@PathParam("name") String name) {
        //TODO ... remove user here ...
        // redirect to the UserManager (the previous object in the request chain)
        return redirect(getPrevious().getPath());
    }

    /**
     * This method is not implemented but demonstrates how PUT requests can be used
     */
    @PUT
    public Object doPut(@PathParam("name") String name) {
        //TODO ... update user here ...
        // redirect to myself
        return redirect(getPath());
    }

}

