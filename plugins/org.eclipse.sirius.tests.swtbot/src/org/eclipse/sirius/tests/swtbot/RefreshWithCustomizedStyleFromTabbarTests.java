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
package org.eclipse.sirius.tests.swtbot;

import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.tests.support.api.TestsUtil;
import org.eclipse.sirius.tests.swtbot.support.utils.SWTBotUtils;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Tests ensuring that customizing styles through the tabbar works as expected.
 * 
 * @author alagarde
 */
public class RefreshWithCustomizedStyleFromTabbarTests extends AbstractRefreshWithCustomizedStyleOnCompleteExampleTest {

    /**
     * Ensures that changing the routing style for an edge from the appearance
     * page works as expected (and also tests that the style is considered as
     * custom).
     * 
     * @throws Exception
     *             Test error.
     */
    public void testChangeRoutingStyleFromTabbar() throws Exception {
        // Not available in fixed tabbar
        if (!TestsUtil.isDynamicTabbar()) {
            return;
        }

        editor.reveal(referenceEditPartBot.part());
        referenceEditPartBot.select();

        final Predicate<SWTBotGefEditPart> modifiedStatePredicate = new Predicate<SWTBotGefEditPart>() {

            public boolean apply(SWTBotGefEditPart input) {
                Routing routing = ((org.eclipse.gmf.runtime.notation.ConnectorStyle) ((View) input.part().getModel()).getStyles().iterator().next()).getRouting();
                return routing.getValue() == Routing.TREE;
            }
        };
        final Predicate<SWTBotGefEditPart> initialStatePredicate = Predicates.not(modifiedStatePredicate);
        doTestStyleCustomizationThroughRoutingStyleSelectionFromTabbar(referenceEditPartBot, "eClass2", initialStatePredicate, modifiedStatePredicate, "Tree Style Routing");
    }

    /**
     * Ensures that changing the background image of a figure from the tabbar
     * works as expected (and also tests that the style is considered as
     * custom).
     * 
     * @throws Exception
     *             Test error.
     */
    public void testChangeBackgroundImageFromTabbar() throws Exception {
        // Not available in fixed tabbar
        if (!TestsUtil.isDynamicTabbar()) {
            return;
        }
        editor.reveal(eClass1WithSquareStyleBot.part());
        eClass1WithSquareStyleBot.select();

        final Predicate<SWTBotGefEditPart> stateWhenBackgroundImageIsChangedPredicate = new Predicate<SWTBotGefEditPart>() {

            public boolean apply(SWTBotGefEditPart input) {
                return getWorkspaceImage(input) != null;
            }
        };
        final Predicate<SWTBotGefEditPart> stateWithInitialBackgroundImagePredicate = Predicates.not(stateWhenBackgroundImageIsChangedPredicate);
        doTestStyleCustomizationThroughBackgroundImageFromTabbar(eClass1WithSquareStyleBot, stateWithInitialBackgroundImagePredicate, stateWhenBackgroundImageIsChangedPredicate, NEW_IMAGE_NAME);
    }

    /**
     * Ensures that changing the line color of a figure from the tabbar works as
     * expected (and also tests that the style is considered as custom).
     * 
     * @throws Exception
     *             Test error.
     */
    public void testChangeLineColorFromTabbar() throws Exception {
        editor.reveal(eClass1WithSquareStyleBot.part());
        eClass1WithSquareStyleBot.select();
        SWTBotUtils.waitAllUiEvents();
        final Predicate<SWTBotGefEditPart> stateWhenBackgroundColorIsChangedPredicate = new Predicate<SWTBotGefEditPart>() {

            public boolean apply(SWTBotGefEditPart input) {
                int lineColor = ((org.eclipse.gmf.runtime.notation.ShapeStyle) ((View) input.part().getModel()).getStyles().iterator().next()).getLineColor();
                return lineColor == 8905185;
            }
        };
        final Predicate<SWTBotGefEditPart> stateWithInitialBackgroundColorPredicate = Predicates.not(stateWhenBackgroundColorIsChangedPredicate);
        doTestStyleCustomizationThroughColorSelectionFromTabbar(eClass1WithSquareStyleBot, "Li&ne Color", stateWithInitialBackgroundColorPredicate, stateWhenBackgroundColorIsChangedPredicate,
                "Yellow");
    }

    /**
     * Ensures that changing the background color of a figure from the tabbar
     * works as expected (and also tests that the style is considered as
     * custom).
     * 
     * @throws Exception
     *             Test error.
     */
    public void testChangeBackgroundColorFromTabbar() throws Exception {
        editor.reveal(eClass1WithSquareStyleBot.part());
        eClass1WithSquareStyleBot.select();
        SWTBotUtils.waitAllUiEvents();
        final Predicate<SWTBotGefEditPart> stateWhenBackgroundColorIsChangedPredicate = new Predicate<SWTBotGefEditPart>() {

            public boolean apply(SWTBotGefEditPart input) {
                int bgColor = ((org.eclipse.gmf.runtime.notation.ShapeStyle) ((View) input.part().getModel()).getStyles().iterator().next()).getFillColor();
                return bgColor == 8905185;
            }
        };
        final Predicate<SWTBotGefEditPart> stateWithInitialBackgroundColorPredicate = Predicates.not(stateWhenBackgroundColorIsChangedPredicate);
        doTestStyleCustomizationThroughColorSelectionFromTabbar(eClass1WithSquareStyleBot, "Fill &Color", stateWithInitialBackgroundColorPredicate, stateWhenBackgroundColorIsChangedPredicate,
                "Yellow");
    }

}
