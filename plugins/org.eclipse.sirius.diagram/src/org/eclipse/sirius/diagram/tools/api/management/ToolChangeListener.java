/*******************************************************************************
 * Copyright (c) 2018 Obeo
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.tools.api.management;

/**
 * Listen to any change regarding available tools and their status(visible, filtered).
 * 
 * @author <a href="mailto:pierre.guilet@obeo.fr">Pierre Guilet</a>
 *
 */
public interface ToolChangeListener {

    /**
     * Define the kind of change that can trigger tool changes.
     * 
     * @author <a href="mailto:pierre.guilet@obeo.fr">Pierre Guilet</a>
     *
     */
    enum ChangeKind {
        /**
         * The tool changes have been triggered by a VSM update.
         */
        VSM_UPDATE,
        /**
         * The tool changes have not been triggered by a VSM update.
         */
        OTHER_UPDATE
    }

    /**
     * Notifies that tool(s) have been updated.
     * 
     * @param changeKind
     *            the kind of change that triggered the tool changes.
     */
    void notifyToolChange(ChangeKind changeKind);
}
