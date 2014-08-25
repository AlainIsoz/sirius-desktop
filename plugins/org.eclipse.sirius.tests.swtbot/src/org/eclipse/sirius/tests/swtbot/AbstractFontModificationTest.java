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

import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.DDiagramElementContainer;
import org.eclipse.sirius.diagram.DEdge;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramEdgeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramListEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramNameEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SiriusWrapLabel;
import org.eclipse.sirius.tests.swtbot.support.api.business.UILocalSession;
import org.eclipse.sirius.tests.swtbot.support.api.business.UIResource;
import org.eclipse.sirius.tests.swtbot.support.utils.SWTBotUtils;
import org.eclipse.sirius.viewpoint.BasicLabelStyle;
import org.eclipse.sirius.viewpoint.FontFormat;
import org.eclipse.sirius.viewpoint.LabelStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarToggleButton;

/**
 * Tool classes for all Font Tests.
 * 
 * @author jdupont
 */
public class AbstractFontModificationTest extends AbstractRefreshWithCustomizedStyleTest {

    private static final String MODEL = "tc2250.ecore";

    private static final String SESSION_FILE = "tc2250.aird";

    private static final String DATA_UNIT_DIR = "data/unit/font/";

    private static final String FILE_DIR = "/";

    private static final String REPRESENTATION_INSTANCE_NAME = "2250";

    private static final String REPRESENTATION_NAME = "Entities";

    private UIResource sessionAirdResource;

    private UILocalSession localSession;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSetUpBeforeClosingWelcomePage() throws Exception {
        super.onSetUpBeforeClosingWelcomePage();
        copyFileToTestProject(Activator.PLUGIN_ID, DATA_UNIT_DIR, MODEL, SESSION_FILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSetUpAfterOpeningDesignerPerspective() throws Exception {
        super.onSetUpAfterOpeningDesignerPerspective();
        sessionAirdResource = new UIResource(designerProject, FILE_DIR, SESSION_FILE);
        localSession = designerPerspective.openSessionFromFile(sessionAirdResource);

        editor = openDiagram(localSession.getOpenedSession(), REPRESENTATION_NAME, REPRESENTATION_INSTANCE_NAME, DDiagram.class);
    }

    /**
     * Ensures that the given {@link SWTBotGefEditPart}'s label is displayed
     * with a 'normal' font (neither bold, italic, underline nor stroke).
     * 
     * @param myEClassEP
     *            the {@link SWTBotGefEditPart} to test
     */
    protected static void checkNormalFontStyle(SWTBotGefEditPart myEClassEP) {
        checkFontStyle(myEClassEP, SWT.NORMAL, SWT.NORMAL, FontFormat.NORMAL, false, false);
    }

    /**
     * Ensures that the given {@link SWTBotGefEditPart}'s label is displayed as
     * bold.
     * 
     * @param myEClassEP
     *            the {@link SWTBotGefEditPart} to test
     */
    protected static void checkBoldFontStyle(SWTBotGefEditPart myEClassEP) {
        checkFontStyle(myEClassEP, SWT.BOLD, SWT.BOLD, FontFormat.BOLD, false, false);
    }

    /**
     * Ensures that the given {@link SWTBotGefEditPart}'s label is displayed as
     * italic.
     * 
     * @param myEClassEP
     *            the {@link SWTBotGefEditPart} to test
     */
    protected static void checkItalicFontStyle(SWTBotGefEditPart myEClassEP) {
        checkFontStyle(myEClassEP, SWT.ITALIC, SWT.ITALIC, FontFormat.ITALIC, false, false);
    }

    /**
     * Ensures that the given {@link SWTBotGefEditPart}'s label is displayed as
     * bold and italic.
     * 
     * @param myEClassEP
     *            the {@link SWTBotGefEditPart} to test
     */
    protected void checkBoldItalicFontStyle(SWTBotGefEditPart myEClassEP) {
        checkFontStyle(myEClassEP, SWT.BOLD | SWT.ITALIC, SWT.BOLD | SWT.ITALIC, FontFormat.BOLD_ITALIC, false, false);
    }

    /**
     * Ensures that the given {@link SWTBotGefEditPart}'s label is displayed as
     * specified.
     * 
     * @param editPart
     *            the {@link SWTBotGefEditPart} to test
     * @param d2dStyle
     *            the Draw2D style
     * @param gmfStyle
     *            the gmf style
     * @param viewpointStyle
     *            the viewpoint style
     * @param underlined
     *            true if the given {@link SWTBotGefEditPart}'s label should be
     *            underlined
     * @param strikedThrough
     *            true if the given {@link SWTBotGefEditPart}'s label should be
     *            stroke through
     * 
     */
    protected static void checkFontStyle(SWTBotGefEditPart editPart, int d2dStyle, int gmfStyle, int viewpointStyle, boolean underlined, boolean strikedThrough) {
        checkFontStyle(editPart, d2dStyle, gmfStyle, viewpointStyle, underlined, strikedThrough, null, -1, -1);
    }

    /**
     * Ensures that the given {@link SWTBotGefEditPart}'s label is displayed as
     * specified.
     * 
     * @param editPart
     *            the {@link SWTBotGefEditPart} to test
     * @param d2dStyle
     *            the Draw2D style
     * @param gmfStyle
     *            the gmf style
     * @param viewpointStyle
     *            the viewpoint style
     * @param underlined
     *            true if the given {@link SWTBotGefEditPart}'s label should be
     *            underlined
     * @param strikedThrough
     *            true if the given {@link SWTBotGefEditPart}'s label should be
     *            stroke through
     * @param fontName
     *            the name (null if no specific name is expected) of the font
     *            that should be associated to the given
     *            {@link SWTBotGefEditPart}'s label
     * @param fontSize
     *            the expected size (-1 if no specific size is expected)for the
     *            font associated to the given {@link SWTBotGefEditPart}'s label
     * @param fontColor
     *            the expected color (-1 if no specific color is expected) for
     *            the font associated to the given {@link SWTBotGefEditPart}'s
     *            label
     * 
     */
    protected static void checkFontStyle(SWTBotGefEditPart editPart, int d2dStyle, int gmfStyle, int viewpointStyle, boolean underlined, boolean strikedThrough, String fontName, int fontSize,
            int fontColor) {
        // Check the state of draw2d label, GMF view and Sirius style.
        if (editPart.part() instanceof AbstractDiagramListEditPart) {
            AbstractDiagramListEditPart part = (AbstractDiagramListEditPart) editPart.part();
            AbstractDiagramNameEditPart labelPart = (AbstractDiagramNameEditPart) part.getChildren().get(0);

            assertTrue("The figure of this part should be a SiriusWrapLabel.", labelPart.getFigure() instanceof SiriusWrapLabel);
            SiriusWrapLabel label = (SiriusWrapLabel) labelPart.getFigure();
            checkFont(label, d2dStyle, underlined, strikedThrough);

            Node gmfNode = (Node) part.getNotationView();
            checkFont(gmfNode, gmfStyle, underlined, strikedThrough, fontName, fontSize, fontColor);

            DDiagramElementContainer viewpointNode = (DDiagramElementContainer) part.resolveDiagramElement();
            checkFont(viewpointNode, viewpointStyle);
        } else if (editPart.part() instanceof AbstractDiagramEdgeEditPart) {
            AbstractDiagramEdgeEditPart part = (AbstractDiagramEdgeEditPart) editPart.part();
            AbstractDiagramNameEditPart labelPart = (AbstractDiagramNameEditPart) part.getChildren().get(0);

            assertTrue("The figure of this part should be a SiriusWrapLabel.", labelPart.getFigure() instanceof SiriusWrapLabel);
            SiriusWrapLabel label = (SiriusWrapLabel) labelPart.getFigure();
            checkFont(label, d2dStyle, underlined, strikedThrough);

            Edge gmfEdge = (Edge) part.getNotationView();
            // Node gmfNode = (Node) part.getNotationView();
            checkFont(gmfEdge, gmfStyle, underlined, strikedThrough, fontName, fontSize, fontColor);

            DEdge viewpointEdge = (DEdge) part.resolveDiagramElement();
            checkFont(viewpointEdge, viewpointStyle);
        } else {
            fail("The case of \"" + editPart.part().getClass().getName() + "\" is not managed by this method.");
        }
    }

    /**
     * Ensures that the given {@link View} has the expected style.
     * 
     * @param gmfNode
     *            the {@link View} to test
     * @param style
     *            the gmf style
     * 
     * @param underlined
     *            true if the given {@link SWTBotGefEditPart}'s label should be
     *            underlined
     * @param strikedThrough
     *            true if the given {@link SWTBotGefEditPart}'s label should be
     *            stroke through
     * @param fontName
     *            the name (null if no specific name is expected) of the font
     *            that should be associated to the given
     *            {@link SWTBotGefEditPart}'s label
     * @param fontSize
     *            the expected size (-1 if no specific size is expected)for the
     *            font associated to the given {@link SWTBotGefEditPart}'s label
     * @param fontColor
     *            the expected color (-1 if no specific color is expected) for
     *            the font associated to the given {@link SWTBotGefEditPart}'s
     *            label
     * 
     */
    protected static void checkFont(View gmfNode, int style, boolean underlined, boolean strikedThrough, String fontName, int fontSize, int fontColor) {
        FontStyle gmfFontStyle = (FontStyle) gmfNode.getStyle(NotationPackage.eINSTANCE.getFontStyle());
        boolean italicStatus;
        boolean boldStatus;

        switch (style) {
        case SWT.BOLD:
            italicStatus = false;
            boldStatus = true;
            break;

        case SWT.ITALIC:
            italicStatus = true;
            boldStatus = false;
            break;

        case SWT.BOLD | SWT.ITALIC:
            italicStatus = true;
            boldStatus = true;
            break;

        default:
            italicStatus = false;
            boldStatus = false;
            break;
        }

        assertEquals("Wrong Bold status", boldStatus, gmfFontStyle.isBold());
        assertEquals("Wrong Italic status", italicStatus, gmfFontStyle.isItalic());
        assertEquals("Wrong Underlined status", underlined, gmfFontStyle.isUnderline());
        assertEquals("Wrong Striked status", strikedThrough, gmfFontStyle.isStrikeThrough());
        if (fontName != null) {
            assertEquals("Wrong Font Name", fontName, gmfFontStyle.getFontName());
        }
        if (fontSize != -1) {
            assertEquals("Wrong Font Size", fontSize, gmfFontStyle.getFontHeight());
        }
        if (fontColor != -1) {
            assertEquals("Wrong Font Color", fontColor, gmfFontStyle.getFontColor());
        }
    }

    protected static void checkFont(DDiagramElementContainer viewpointNode, int style) {
        LabelStyle label = viewpointNode.getOwnedStyle();
        assertEquals("Wrong viewpoint font format (" + FontFormat.get(style) + ")", style, label.getLabelFormat().getValue());
    }

    protected static void checkFont(DEdge viewpointEdge, int style) {
        BasicLabelStyle label = viewpointEdge.getOwnedStyle().getCenterLabelStyle();
        assertEquals("Wrong viewpoint font format (" + FontFormat.get(style) + ")", style, label.getLabelFormat().getValue());
    }

    protected static void checkFont(SiriusWrapLabel label, int swtStyle, boolean underlined, boolean strikedThrough) {
        assertEquals("Wrong figure font style", swtStyle, label.getFont().getFontData()[0].getStyle());
        assertEquals("Wrong figure font style", strikedThrough, label.isTextStrikedThrough());
        assertEquals("Wrong figure font style", underlined, label.isTextUnderlined());
    }

    /**
     * Returns the 'bold' button from tabbar.
     * 
     * @return the 'bold' button from tabbar.
     */
    protected SWTBotToolbarToggleButton getTabbarBoldButton() {
        return bot.toolbarToggleButtonWithTooltip("Bold Font Style");
    }

    /**
     * Returns the 'italic' button from tabbar.
     * 
     * @return the 'italic' button from tabbar.
     */
    protected SWTBotToolbarToggleButton getTabbarItalicButton() {
        return bot.toolbarToggleButtonWithTooltip("Italic Font Style");
    }

    /**
     * Opens the 'Font' pop-up from the tabbar
     * 
     * @return the bot handling the opened 'Font' pop-up
     */
    protected SWTBot openFontPopupFromTabbar() {
        bot.toolbarButtonWithTooltip("Font").click();

        SWTBotUtils.waitAllUiEvents();
        SWTBot bot2 = bot.activeShell().bot();
        return bot2;
    }

    /**
     * Closes the font popup opened from tabbar by clicking on "OK".
     */
    protected void closeFontPopup() {
        SWTBotShell fontShell = bot.activeShell();
        bot.button("OK").click();
        bot.waitUntil(Conditions.shellCloses(fontShell));
        SWTBotUtils.waitAllUiEvents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        localSession = null;
        sessionAirdResource = null;
        super.tearDown();
    }
}
