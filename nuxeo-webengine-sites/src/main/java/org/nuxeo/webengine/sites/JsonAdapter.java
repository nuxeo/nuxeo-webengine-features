/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.webengine.sites;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.model.impl.DefaultAdapter;

import static org.nuxeo.webengine.sites.utils.SiteConstants.*;

/**
 * Adapter service for the tree in sites module.
 */
@WebAdapter(name = "json", type = "JsonTreeAdapter", facets = {"Site"})
public class JsonAdapter extends DefaultAdapter {

    public static final String NAVIGATOR_TREE = "navigatorTree";
    public static final String ROOT_DOCUMENT = "siteName";

    @GET
    public Response doGet(@QueryParam("root") String root) {
        WebContext ctx = WebEngine.getActiveContext();
        Object o = ctx.getTargetObject();

        DocumentModel rootDoc = null;
        DocumentModel currentDoc = null;

        if (o instanceof Site) {
            Site site = (Site) o;
            rootDoc = site.getDocument();
            currentDoc = rootDoc;
        } else if (o instanceof DocumentObject) {
            DocumentObject docObj = (DocumentObject) o;
            currentDoc = docObj.getDocument();
            rootDoc = getTreeRoot(currentDoc);
        }

        if (rootDoc != null) {
            DocumentModel d = (DocumentModel) ctx.getUserSession().get(ROOT_DOCUMENT);
            if (d == null || !d.equals(rootDoc)) {
                ctx.getUserSession().put(ROOT_DOCUMENT, rootDoc);
            }
            SiteDocumentTree tree = new SiteDocumentTree(ctx, rootDoc);
            String result = "";
            if (root == null || "source".equals(root)) {
                Path relPath = getRelativePath(rootDoc, currentDoc);
                tree.enter(ctx, relPath.toString());
                result = tree.getTreeAsJSONArray(ctx);
            } else {
                result = tree.enter(ctx, root);
            }
            return Response.ok().entity(result).build();
        }
        return null;
    }

    public static Path getRelativePath(DocumentModel rootDoc, DocumentModel doc) {
        Path rootPath = rootDoc.getPath();
        Path docPath = doc.getPath();
        int n = rootPath.segmentCount();
        if (docPath.matchingFirstSegments(rootPath) == n) {
            return docPath.removeFirstSegments(n);
        }
        return null;
    }

    protected static DocumentModel getTreeRoot(DocumentModel doc) {
        if (doc != null) {
            CoreSession session = CoreInstance.getInstance().getSession(
                    doc.getSessionId());
            DocumentModel parent = doc;
            while (parent != null) {
                // Check for 'WebView' facet. This is enough since only
                // Workspace and WebSite documents have this facet.
                if (parent.hasFacet(WEB_VIEW_FACET)) {
                    return parent;
                }
                try {
                    parent = session.getDocument(parent.getParentRef());
                } catch (ClientException e) {
                    return null;
                }
            }
        }
        return null;
    }

}
