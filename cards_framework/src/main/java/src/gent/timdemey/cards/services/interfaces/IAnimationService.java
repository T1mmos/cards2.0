package gent.timdemey.cards.services.interfaces;

import javax.swing.JComponent;

import gent.timdemey.cards.ui.panels.IPanelManager;

public interface IAnimationService
{
    void animate (JComponent comp, IPanelManager pm);
    void stopAnimate(JComponent jcomp);
}
