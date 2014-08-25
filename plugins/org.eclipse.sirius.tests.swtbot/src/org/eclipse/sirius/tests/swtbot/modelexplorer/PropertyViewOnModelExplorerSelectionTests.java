/*******************************************************************************
 * Copyright (c) 2010, 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.tests.swtbot.modelexplorer;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tests.swtbot.support.api.AbstractSiriusSwtBotGefTestCase;
import org.eclipse.sirius.tests.swtbot.support.api.business.UIResource;
import org.eclipse.sirius.tests.swtbot.support.api.condition.TreeItemTextCondition;
import org.eclipse.sirius.ui.tools.api.views.modelexplorerview.IModelExplorerView;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.matchers.IsInstanceOf;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.eclipse.sirius.tests.swtbot.Activator;
import org.eclipse.sirius.tests.unit.diagram.modeler.ecore.EcoreModeler;

/**
 * Test the use of property view on ModelExplorerView {@link EObject} selection.
 * 
 * See VP-3832.
 * 
 * @author <a href="mailto:esteban.dugueperoux@obeo.fr">Esteban Dugueperoux</a>
 */
public class PropertyViewOnModelExplorerSelectionTests extends AbstractSiriusSwtBotGefTestCase {

    private static final String SEMANTIC_RESOURCE_NAME = "VP-3832.ecore";

    private static final String REPRESENTATIONS_RESOURCE_NAME = "VP-3832.aird";

    private static final String PATH = "data/unit/VP-3832/";

    private UIResource sessionAirdResource;

    private SWTBot modelExplorerViewBot;

    private SWTBot propertyViewBot;

    @Override
    protected void onSetUpBeforeClosingWelcomePage() throws Exception {
        copyFileToTestProject(Activator.PLUGIN_ID, PATH, SEMANTIC_RESOURCE_NAME, REPRESENTATIONS_RESOURCE_NAME);
    }

    @Override
    protected void onSetUpAfterOpeningDesignerPerspective() throws Exception {
        sessionAirdResource = new UIResource(designerProject, REPRESENTATIONS_RESOURCE_NAME);
        localSession = designerPerspective.openSessionFromFile(sessionAirdResource, true);
        
        SWTBotView modelExplorerView = bot.viewById(IModelExplorerView.ID);
        modelExplorerView.setFocus();
        modelExplorerViewBot = modelExplorerView.bot();

        SWTBotView propertyView = bot.viewById("org.eclipse.ui.views.PropertySheet");
        propertyView.setFocus();
        propertyViewBot = propertyView.bot();
    }

    /**
     * Test the display and the edition of properties in properties view on
     * ModelExplorerView EObject selection.
     */
    public void testPropertyViewEditionOnModelExplorerViewSelection() {
        // Check that properties view display something on treeItem selection of
        // "Representations per category" parent treeItem
        SWTBotTreeItem projectTreeItemBot = modelExplorerViewBot.tree().expandNode(getProjectName(), true);
        SWTBotTreeItem representationsResourceTreeItemBot = projectTreeItemBot.getNode(REPRESENTATIONS_RESOURCE_NAME);
        representationsResourceTreeItemBot.select();
        assertEmptyPropertiesView(true);
        SWTBotTreeItem viewpointTreeItemBot = representationsResourceTreeItemBot.getNode(0).getNode(EcoreModeler.DESIGN_VIEWPOINT_NAME);
        viewpointTreeItemBot.select();
        assertEmptyPropertiesView(true);
        SWTBotTreeItem representationDescriptionTreeItemBot = viewpointTreeItemBot.getNode(0);
        representationDescriptionTreeItemBot.select();
        assertEmptyPropertiesView(true);
        SWTBotTreeItem representationTreeItemBot = representationDescriptionTreeItemBot.getNode(0);
        representationTreeItemBot.select();
        assertEmptyPropertiesView(true);

        // Check that properties view display something on treeItem selection of
        // semantic resource treeItem
        SWTBotTreeItem semanticResourceTreeItemBot = representationsResourceTreeItemBot.getNode(1);
        semanticResourceTreeItemBot.select();
        assertEmptyPropertiesView(true);
        SWTBotTreeItem rootEPackageTreeItemBot = semanticResourceTreeItemBot.getNode(0);
        rootEPackageTreeItemBot.select();
        assertEmptyPropertiesView(false);
        SWTBotTreeItem packageEntitiesRepTreeItemBot = rootEPackageTreeItemBot.getNode(0);
        packageEntitiesRepTreeItemBot.select();
        assertEmptyPropertiesView(true);
        SWTBotTreeItem newEClass1TreeItemBot = rootEPackageTreeItemBot.getNode("NewEClass1");
        newEClass1TreeItemBot.select();
        assertEmptyPropertiesView(false);

        // Check that properties of semantic element named "NewEClass1" are
        // editable
        propertyViewBot.tree().getTreeItem("Name").select().doubleClick();
        String newName = "renamedEClass";
        propertyViewBot.text().setText(newName);
        newEClass1TreeItemBot.select();
        bot.waitUntil(new TreeItemTextCondition(newEClass1TreeItemBot, newName));
        assertEquals("The semantic element rename through properties view should impact the label displayed in the Model Explorer view", newName, newEClass1TreeItemBot.getText());
    }

    private void assertEmptyPropertiesView(boolean emptyPropertiesView) {
        List<Tree> findedTrees = propertyViewBot.getFinder().findControls(IsInstanceOf.instanceOf(Tree.class));
        assertEquals("The properties view is expected to be " + (emptyPropertiesView ? "" : "not") + " empty", emptyPropertiesView, findedTrees.isEmpty()
                || (!findedTrees.isEmpty() && !propertyViewBot.tree().hasItems()));
    }

    @Override
    public void tearDown() throws Exception {
        modelExplorerViewBot.text().setText("");
        sessionAirdResource = null;
        modelExplorerViewBot = null;
        super.tearDown();
    }

}
