/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.webengine.admin;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.NuxeoGroupImpl;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.api.Framework;

@WebObject(type = "UserManager")
@Produces("text/html;charset=UTF-8")
public class UserService extends DefaultObject {

    @GET
    public Object getIndex(@QueryParam("query") String query, @QueryParam("group") String group) {
        if (query != null && !query.isEmpty()) {
            UserManager userManager = Framework.getService(UserManager.class);
            if (group != null) {
                DocumentModelList results = userManager.searchGroups(query);
                return getView("index").arg("groups", results);
            } else {
                List<NuxeoPrincipal> results = userManager.searchPrincipals(query);
                return getView("index").arg("users", results);
            }
        }
        return getView("index");
    }

    @Path("user/{user}")
    public Object searchUsers(@PathParam("user") String user) {
        UserManager userManager = Framework.getService(UserManager.class);
        NuxeoPrincipalImpl principal = (NuxeoPrincipalImpl) userManager.getPrincipal(user);
        if (principal == null) {
            throw new WebResourceNotFoundException("User not found: " + user);
        }
        return newObject("User", principal);
    }

    @Path("group/{group}")
    public Object searchGroups(@PathParam("group") String group) {
        UserManager userManager = Framework.getService(UserManager.class);
        // FIXME: find better name for it
        NuxeoGroup principal = userManager.getGroup(group);
        if (principal == null) {
            throw new WebResourceNotFoundException("Group not found: " + group);
        }
        return newObject("Group", principal);
    }

    @POST
    @Path("user")
    public Response postUser() {
        HttpServletRequest req = ctx.getRequest();
        String username = req.getParameter("username");
        UserManager userManager = Framework.getService(UserManager.class);
        if (username != null && !username.isEmpty()) {
            NuxeoPrincipalImpl user = (NuxeoPrincipalImpl) userManager.getPrincipal(username);
            String[] selectedGroups;
            if (user != null) {
                // update
                user.setFirstName(req.getParameter("firstName"));
                user.setLastName(req.getParameter("lastName"));
                user.setPassword(req.getParameter("password"));
                user.setEmail(req.getParameter("email"));

                selectedGroups = req.getParameterValues("groups");
                List<String> listGroups = Arrays.asList(selectedGroups);
                user.setGroups(listGroups);

                userManager.updatePrincipal(user);
            } else {
                // create
                user = new NuxeoPrincipalImpl(req.getParameter("username"));
                user.setFirstName(req.getParameter("firstName"));
                user.setLastName(req.getParameter("lastName"));
                user.setPassword(req.getParameter("password"));
                user.setEmail(req.getParameter("email"));

                selectedGroups = req.getParameterValues("groups");
                List<String> listGroups = Arrays.asList(selectedGroups);
                user.setGroups(listGroups);

                userManager.createPrincipal(user);
            }
            return redirect(getPath() + "/user/" + user.getName());
        }
        // FIXME
        return null;
    }

    @POST
    @Path("group")
    public Response postGroup() {
        String groupName = ctx.getRequest().getParameter("groupName");
        UserManager userManager = Framework.getService(UserManager.class);
        if (groupName != null && !groupName.equals("")) {
            NuxeoGroup group = new NuxeoGroupImpl(groupName);
            userManager.createGroup(group);
            return redirect(getPath() + "/group/" + group.getName());
        }
        // FIXME
        return null;
    }

    public List<NuxeoGroup> getGroups() {
        return Framework.getService(UserManager.class).getAvailableGroups();
    }

    public List<NuxeoPrincipal> getUsers() {
        return Framework.getService(UserManager.class).getAvailablePrincipals();
    }

}
