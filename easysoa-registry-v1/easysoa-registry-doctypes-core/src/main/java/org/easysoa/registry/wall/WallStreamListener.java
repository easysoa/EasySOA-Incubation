/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Thomas Roger <troger@nuxeo.com>
 */

package org.easysoa.registry.wall;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.*;
import static org.nuxeo.ecm.core.schema.FacetNames.HIDDEN_IN_NAVIGATION;
import static org.nuxeo.ecm.core.schema.FacetNames.SUPER_SPACE;
import static org.nuxeo.ecm.core.schema.FacetNames.SYSTEM_DOCUMENT;
import static org.nuxeo.ecm.platform.comment.api.CommentEvents.COMMENT_ADDED;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.activity.Activity;
import org.nuxeo.ecm.activity.ActivityBuilder;
import org.nuxeo.ecm.activity.ActivityHelper;
import org.nuxeo.ecm.activity.ActivityStreamService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.SystemPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.ShallowDocumentModel;
import org.nuxeo.ecm.platform.publisher.api.PublishingEvent;
import org.nuxeo.runtime.api.Framework;

/**
 * XXX TO BE REMOVED WHEN NXP-9077 BRANCH IS MERGED.
 */
public class WallStreamListener implements PostCommitEventListener {

    private static final Log log = LogFactory.getLog(WallStreamListener.class);

    public static final String WORKFLOW_STARTED_VERB = "workflowStarted";

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        if (shouldHandle(events)) {
            List<Event> filteredEvents = filterDuplicateEvents(events);
            for (Event event : filteredEvents) {
                handleEvent(event);
            }
        }
    }

    private boolean shouldHandle(EventBundle events) {
        return events.containsEventName(DOCUMENT_CREATED)
                || events.containsEventName(DOCUMENT_UPDATED)
                || events.containsEventName(DOCUMENT_REMOVED)
                || events.containsEventName(COMMENT_ADDED)
                || events.containsEventName(DOCUMENT_RESTORED)
                || events.containsEventName(DOCUMENT_CHECKEDOUT)
                || events.containsEventName(PublishingEvent.documentPublished.name());
    }

    private List<Event> filterDuplicateEvents(EventBundle events) {
        List<Event> filteredEvents = new ArrayList<Event>();
        for (Event event : events) {
            filteredEvents = removeEventIfExist(filteredEvents, event);
            filteredEvents.add(event);
        }
        return filteredEvents;
    }

    private List<Event> removeEventIfExist(List<Event> events, Event event) {
        EventContext eventContext = event.getContext();
        if (eventContext instanceof DocumentEventContext) {
            DocumentModel doc = ((DocumentEventContext) eventContext).getSourceDocument();
            for (Iterator<Event> it = events.iterator(); it.hasNext();) {
                Event filteredEvent = it.next();
                EventContext filteredEventContext = filteredEvent.getContext();
                if (filteredEventContext instanceof DocumentEventContext) {
                    DocumentModel filteredEventDoc = ((DocumentEventContext) filteredEventContext).getSourceDocument();
                    if (event.getName().equals(filteredEvent.getName())
                            && doc.getRef().equals(filteredEventDoc.getRef())) {
                        it.remove();
                        break;
                    }
                }
            }
        }
        return events;
    }

    private void handleEvent(Event event) throws ClientException {
        EventContext eventContext = event.getContext();
        if (isSystemPrincipal(eventContext)) {
            // do not log activity for system principal
            return;
        }

        if (!(eventContext instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docEventContext = (DocumentEventContext) eventContext;
        CoreSession session = docEventContext.getCoreSession();
        Principal principal = docEventContext.getPrincipal();
        DocumentModel sourceDoc = docEventContext.getSourceDocument();

        String eventName = event.getName();
        if (isDocumentEvent(eventName) || isCommentEvent(eventName)) {
            addDocumentActivities(session, sourceDoc, principal, eventName);
        } else if (isRestoreEvent(eventName)) {
            addRestoreActivities(session, sourceDoc, principal, eventName);
        } else if (isPublishEvent(eventName)) {
            addPublishActivities(session, sourceDoc, principal, eventName);
        }
    }

    private boolean isSystemPrincipal(EventContext ctx) {
        return ctx.getPrincipal() instanceof SystemPrincipal;
    }

    private boolean isDocumentEvent(String eventName) {
        return DOCUMENT_CREATED.equals(eventName)
                || DOCUMENT_UPDATED.equals(eventName)
                || DOCUMENT_REMOVED.equals(eventName)
                || DOCUMENT_CHECKEDOUT.equals(eventName);
    }

    private boolean isCommentEvent(String eventName) {
        return COMMENT_ADDED.equals(eventName);
    }

    private boolean isRestoreEvent(String eventName) {
        return DOCUMENT_RESTORED.equals(eventName);
    }

    private boolean isPublishEvent(String eventName) {
        return PublishingEvent.documentPublished.name().equals(eventName);
    }

    private void addDocumentActivities(CoreSession session, DocumentModel doc,
                                       Principal principal, String eventName) throws ClientException {
        if (doc instanceof ShallowDocumentModel
                || doc.hasFacet(HIDDEN_IN_NAVIGATION)
                || doc.hasFacet(SYSTEM_DOCUMENT) || doc.isProxy()
                || doc.isVersion()) {
            // Not really interested in non live document or if document
            // cannot be reconnected
            // or if not visible
            return;
        }

        Activity activity = newDocumentActivity(session, doc, principal,
                eventName);
        addActivitiesWithContext(activity, session, doc);
    }

    private Activity newDocumentActivity(CoreSession session,
                                         DocumentModel doc, Principal principal, String eventName) {
        return new ActivityBuilder().actor(
                ActivityHelper.createUserActivityObject(principal)).displayActor(
                ActivityHelper.generateDisplayName(principal)).verb(eventName).object(
                ActivityHelper.createDocumentActivityObject(doc)).displayObject(
                ActivityHelper.getDocumentTitle(doc)).target(
                ActivityHelper.createDocumentActivityObject(
                        doc.getRepositoryName(), doc.getParentRef().toString())).displayTarget(
                getDocumentTitle(session, doc.getParentRef())).build();
    }

    private void addActivitiesWithContext(Activity baseActivity,
                                          CoreSession session, DocumentModel doc) throws ClientException {
        List<Activity> activities = computeActivitiesWithContext(baseActivity,
                session, doc);
        activities.add(0, baseActivity);
        ActivityStreamService activityStreamService = Framework.getLocalService(ActivityStreamService.class);
        for (Activity activity : activities) {
            activityStreamService.addActivity(activity);
        }
    }

    private List<Activity> computeActivitiesWithContext(Activity activity,
                                                        CoreSession session, DocumentModel doc) throws ClientException {
        List<Activity> activities = new ArrayList<Activity>();
        for (DocumentRef ref : getParentSuperSpaceRefs(session, doc)) {
            String context = ActivityHelper.createDocumentActivityObject(
                    session.getRepositoryName(), ref.toString());
            activities.add(new ActivityBuilder(activity).context(context).build());
        }
        return activities;
    }

    private void addRestoreActivities(CoreSession session, DocumentModel doc,
                                      Principal principal, String eventName) throws ClientException {
        if (doc instanceof ShallowDocumentModel
                || doc.hasFacet(HIDDEN_IN_NAVIGATION)
                || doc.hasFacet(SYSTEM_DOCUMENT) || doc.isProxy()
                || doc.isVersion()) {
            // Not really interested in non live document or if document
            // cannot be reconnected
            // or if not visible
            return;
        }

        Activity activity = newRestoreActivity(doc, principal, eventName);
        addActivitiesWithContext(activity, session, doc);
    }

    private Activity newRestoreActivity(DocumentModel doc, Principal principal,
                                        String eventName) {
        return new ActivityBuilder().actor(
                ActivityHelper.createUserActivityObject(principal)).displayActor(
                ActivityHelper.generateDisplayName(principal)).verb(eventName).object(
                doc.getVersionLabel()).target(
                ActivityHelper.createDocumentActivityObject(doc)).displayTarget(
                ActivityHelper.getDocumentTitle(doc)).build();
    }

    private void addPublishActivities(CoreSession session, DocumentModel doc,
                                      Principal principal, String eventName) throws ClientException {
        if (doc.isProxy()) {
            DocumentModel version = session.getSourceDocument(doc.getRef());
            DocumentModel liveDoc = session.getSourceDocument(version.getRef());

            Activity activity = newPublishActivity(session, doc, liveDoc,
                    principal, eventName);
            addActivitiesWithContext(activity, session, doc);
        }
    }

    private Activity newPublishActivity(CoreSession session,
                                        DocumentModel proxy, DocumentModel liveDoc, Principal principal,
                                        String eventName) {
        return new ActivityBuilder().actor(
                ActivityHelper.createUserActivityObject(principal)).displayActor(
                ActivityHelper.generateDisplayName(principal)).verb(eventName).object(
                ActivityHelper.createDocumentActivityObject(liveDoc)).displayObject(
                ActivityHelper.getDocumentTitle(liveDoc)).target(
                ActivityHelper.createDocumentActivityObject(
                        proxy.getRepositoryName(),
                        proxy.getParentRef().toString())).displayTarget(
                getDocumentTitle(session, proxy.getParentRef())).build();
    }

    private String getDocumentTitle(CoreSession session, DocumentRef docRef) {
        try {
            DocumentModel doc = session.getDocument(docRef);
            return ActivityHelper.getDocumentTitle(doc);
        } catch (ClientException e) {
            return docRef.toString();
        }
    }

    private List<DocumentRef> getParentSuperSpaceRefs(CoreSession session,
                                                      final DocumentModel doc) throws ClientException {
        final List<DocumentRef> parents = new ArrayList<DocumentRef>();
        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() throws ClientException {
                List<DocumentModel> parentDocuments = session.getParentDocuments(doc.getRef());
                for (DocumentModel parent : parentDocuments) {
                    if (parent.hasFacet(SUPER_SPACE)) {
                        parents.add(parent.getRef());
                    }
                }
            }
        }.runUnrestricted();
        return parents;
    }

}
