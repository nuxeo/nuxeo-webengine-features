/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.webengine.sites.fragments;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.tag.TagService;
import org.nuxeo.ecm.platform.tag.WeightedTag;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.theme.fragments.AbstractFragment;
import org.nuxeo.theme.models.Model;
import org.nuxeo.theme.models.ModelException;
import org.nuxeo.webengine.sites.models.TagCloudListModel;
import org.nuxeo.webengine.sites.models.TagCloudModel;
import org.nuxeo.webengine.sites.utils.SiteUtils;

/**
 * Action fragment for initializing the fragment related to the details about
 * the tag cloud.
 *
 * @author rux
 */
public class TagCloudFragment extends AbstractFragment {

    /**
     * Returns the details about the tag cloud that have been created under a
     * webpage.
     */
    @Override
    public Model getModel() throws ModelException {
        TagCloudListModel model = new TagCloudListModel();
        try {
            TagService tagService = Framework.getService(TagService.class);
            if (tagService != null && WebEngine.getActiveContext() != null && tagService.isEnabled()) {
                WebContext ctx = WebEngine.getActiveContext();
                CoreSession session = ctx.getCoreSession();
                DocumentModel documentModel = ctx.getTargetObject().getAdapter(
                        DocumentModel.class);

                DocumentModel siteDocument = SiteUtils.getFirstWebSiteParent(
                        session, documentModel);
                TagCloudModel tagCloudModel = null;
                List<WeightedTag> tagCloud = tagService.getPopularCloud(
                        session, siteDocument);
                if (tagCloud != null && !tagCloud.isEmpty()) {
                    for (WeightedTag weightedTag : tagCloud) {
                        tagCloudModel = new TagCloudModel(
                                weightedTag.tagLabel,
                                weightedTag.tagId,
                                weightedTag.weight);
                        model.addItem(tagCloudModel);
                    }
                }
            }
        } catch (Exception e) {
            throw new ModelException(e);
        }
        return model;
    }

}
