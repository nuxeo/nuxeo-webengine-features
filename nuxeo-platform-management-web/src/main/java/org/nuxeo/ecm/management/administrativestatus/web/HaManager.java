/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     mcedica
 */
package org.nuxeo.ecm.management.administrativestatus.web;

import static org.nuxeo.ecm.platform.management.web.utils.PlatformManagementWebConstants.ADMINISTRATIVE_STATUS_WEB_OBJECT_TYPE;
import static org.nuxeo.ecm.platform.management.web.utils.PlatformManagementWebConstants.PROBES_WEB_OBJECT_TYPE;
import static org.nuxeo.ecm.platform.management.web.utils.PlatformManagementWebConstants.PROBE_WEB_OBJECT_TYPE;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.management.probes.ProbeRunner;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.model.Access;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.api.Framework;


/**
 * 
 * Web object implementation corresponding to the root module for ha 
 * (module used for administrative purpose)
 * 
 * @author mcedica
 * 
 */
@WebObject(type = "ha" , administrator=Access.GRANT)
@Produces("text/html; charset=UTF-8")
public class HaManager extends DefaultObject {

    private static final Log log = LogFactory.getLog(HaManager.class);

    private ProbeRunner probeRunner;

    @Override
    public void initialize(Object... args) {
        try {
            probeRunner = getProbeRunner();
        } catch (Exception e) {
            log.error("Unable to retreive the probeRunner", e);
        }
    }

    @GET
    public Object doGet() {
        return getView("index");
    }

    @Path("{probeName}")
    public Object dispatch(@PathParam("probeName") String path) {
        try {
            if (getProbesObjectTypeName().equals(path)) {
                return newObject(getProbesObjectTypeName(), probeRunner);
            }

            if (getAdministrativeStatusObjectTypeName().equals(path)) {
                return newObject(getAdministrativeStatusObjectTypeName(), path);
            } else {
                return newObject(getProbeObjectTypeName(), probeRunner, path);
            }

        } catch (Exception e) {
            throw WebException.wrap(e);
        }
    }

    ProbeRunner getProbeRunner() throws Exception {
        if (probeRunner == null) {
            probeRunner = Framework.getService(ProbeRunner.class);
        }
        return probeRunner;
    }

    public String getAdministrativeStatusObjectTypeName() {
        return ADMINISTRATIVE_STATUS_WEB_OBJECT_TYPE;
    }

    public String getProbeObjectTypeName() {
        return PROBE_WEB_OBJECT_TYPE;
    }

    public String getProbesObjectTypeName() {
        return PROBES_WEB_OBJECT_TYPE;
    }
}