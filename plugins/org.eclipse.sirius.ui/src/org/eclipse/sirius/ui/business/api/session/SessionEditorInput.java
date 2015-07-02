/*******************************************************************************
 * Copyright (c) 2007, 2015 THALES GLOBAL SERVICES and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.ui.business.api.session;

import java.lang.ref.WeakReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.business.api.session.factory.SessionFactory;
import org.eclipse.sirius.viewpoint.provider.SiriusEditPlugin;
import org.eclipse.ui.IMemento;

/**
 * Specific URI editor input providing the session data.
 * 
 * @author cbrun
 */
public class SessionEditorInput extends URIEditorInput {

    /**
     * Constant to store the uri of the main resource of the session. We can't
     * use the URI that is already store in the URIEditorInput because it can be
     * different in case of fragmentation.
     */
    private static final String SESSION_RESOURCE_URI = "SESSION_RESOURCE_URI";

    /**
     * Default editor name
     * 
     * @since 0.9.0
     */
    private static final String DEFAULT_EDITOR_NAME = "Representation";

    private WeakReference<Session> sessionRef;

    /**
     * add a name field to override the {@link URIEditorInput} one's with
     * possibility to update it.
     */
    private String name;

    private URI sessionResourceURI;

    private WeakReference<EObject> inputRef;

    /**
     * Create a new SessionEditorInput with the current session and ui session.
     * 
     * @param uri
     *            element URI.
     * @param name
     *            name of the editor.
     * @param session
     *            the current session.
     */
    public SessionEditorInput(final URI uri, final String name, final Session session) {
        super(uri, name);
        this.name = name;
        this.sessionRef = new WeakReference<Session>(session);
        if (session.getSessionResource() != null) {
            this.sessionResourceURI = session.getSessionResource().getURI();
        }
    }

    /**
     * Create a new SessionEditorInput with a memento.
     * 
     * @param memento
     *            a bit of information kept by the platform.
     * @since 0.9.0
     */
    public SessionEditorInput(final IMemento memento) {
        super(memento);
    }

    /**
     * return the model editing session.
     * 
     * @return the model editing session.
     */
    public Session getSession() {
        Session session = sessionRef != null ? sessionRef.get() : null;
        if (session == null) {
            URI sessionModelURI = getURI().trimFragment();
            if (sessionResourceURI != null) {
                sessionModelURI = sessionResourceURI;
            }
            session = getSession(sessionModelURI);
            if (session != null) {
                this.sessionRef = new WeakReference<Session>(session);
            }
        }
        return session;
    }

    /**
     * Get the input of this editor input.
     * 
     * @return the input of this editor input
     */
    public EObject getInput() {
        EObject input = inputRef != null ? inputRef.get() : null;
        if (input == null) {
            if (getSession() != null && getSession().isOpen() && getURI().hasFragment()) {
                input = getSession().getTransactionalEditingDomain().getResourceSet().getEObject(getURI(), false);
                if (input != null) {
                    inputRef = new WeakReference<EObject>(input);
                }
            }
        }
        return input;
    }

    @Override
    public String getName() {
        return name == null ? super.getName() : name;
    }

    /**
     * 
     * @param string
     */
    void setName(final String string) {
        this.name = string;
    }

    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
        memento.putString(NAME_TAG, getName());
        memento.putString(CLASS_TAG, getClass().getName());
        if (sessionResourceURI != null) {
            memento.putString(SESSION_RESOURCE_URI, sessionResourceURI.toString());
        }
    }

    @Override
    protected void loadState(final IMemento memento) {
        super.loadState(memento);
        setName(memento.getString(NAME_TAG));
        final String sessionResourceURIString = memento.getString(SESSION_RESOURCE_URI);
        if (sessionResourceURIString != null) {
            sessionResourceURI = URI.createURI(sessionResourceURIString);
            Session newSession = getSession(sessionResourceURI);
            if (newSession != null) {
                this.sessionRef = new WeakReference<Session>(newSession);
            }
        }
    }

    /**
     * Get the session.
     * 
     * @param sessionModelURI
     *            the Session Resource URI
     * @return the session if it can be found, <code>null</code> otherwise
     * 
     * @since 0.9.0
     */
    protected Session getSession(URI sessionModelURI) {
        Session sessionFromURI;
        try {
            sessionFromURI = SessionManager.INSTANCE.getSession(sessionModelURI, new NullProgressMonitor());
            if (sessionFromURI != null) {
                if (!sessionFromURI.isOpen()) {
                    sessionFromURI.open(new NullProgressMonitor());
                }
                IEditingSession uiSession = SessionUIManager.INSTANCE.getOrCreateUISession(sessionFromURI);
                uiSession.open();
            }
        } catch (IllegalStateException e) {
            sessionFromURI = null;
            // Silent catch: can happen if failing to retrieve the session from
            // its URI
        } catch (OperationCanceledException e) {
            sessionFromURI = null;
            // Silent catch: can happen if failing to retrieve the session from
            // its URI
        }
        return sessionFromURI;
    }

    @Override
    protected String getBundleSymbolicName() {
        return SiriusEditPlugin.getPlugin().getSymbolicName();
    }

    @Override
    public String getFactoryId() {
        return SessionEditorInputFactory.ID;
    }

    /**
     * Create a new input from an Analysis uri.
     * 
     * @param sessionResourceURI
     *            a session Resource URI.
     * @return a new SessionEditorinput.
     * 
     * @since 0.9.0
     */
    public static SessionEditorInput create(final URI sessionResourceURI) {
        Session session;
        try {
            session = SessionFactory.INSTANCE.createSession(sessionResourceURI, new NullProgressMonitor());
        } catch (CoreException e) {
            return null;
        }
        return new SessionEditorInput(sessionResourceURI, DEFAULT_EDITOR_NAME, session);
    }

    @Override
    public String getToolTipText() {
        return getURI().trimFragment().toString() + "/" + getName();
    }

    /**
     * To avoid memory leak, the session is not kept during the closing of the
     * corresponding editor. Indeed, editorInput is kept by
     * {@link org.eclipse.ui.INavigationHistory} and
     * org.eclipse.ui.internal.EditorHistory. This method must not be called by
     * client, it is automatically called by the dispose of
     * {@link DDiagramEditor}.
     * 
     * @deprecated since a {@link org.eclipse.ui.IEditorInput} can be reused by
     *             several instances of {@link org.eclipse.ui.IEditorPart}
     *             through the navigation history view.
     */
    @Deprecated
    public void dispose() {
    }

    /**
     * Super class URIEditorInput only check for existence of local URIs, and
     * this behavior is not acceptable for collaborative sessions.
     * 
     * Instead we only want to ask the session knows about this URI.
     * 
     * This caused a bug when saving/restoring editor state in workbench memento
     * state with collaborative sessions (URI with "cdo" scheme)
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean exists() {
        boolean exists = super.exists();
        if (!exists && getSession() != null) {
            URI resourceURI = getURI().trimFragment();
            for (Resource resource : getSession().getAllSessionResources()) {
                if (resource.getURI().equals(resourceURI)) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        Object a = super.getAdapter(adapter);
        if (IFile.class == adapter && a == null) {
            if (EMFPlugin.IS_RESOURCES_BUNDLE_AVAILABLE) {
                Session inputSession = getSession();
                if (inputSession != null && inputSession.isOpen()) {
                    a = EclipseUtil.getAdatper(adapter, inputSession.getSessionResource().getURI());
                }
            }
        }
        return a;
    }

    @Override
    public int hashCode() {
        if (getInput() != null) {
            return getInput().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SessionEditorInput && getInput() != null) {
            SessionEditorInput otherSessionEditorInput = (SessionEditorInput) o;
            return getInput().equals(otherSessionEditorInput.getInput());
        }
        return super.equals(o);
    }
}
