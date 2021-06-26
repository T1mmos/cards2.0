package gent.timdemey.cards.services.interfaces;

import java.util.Optional;

import javax.swing.JComponent;

import gent.timdemey.cards.ui.panels.IPanelManager;

public interface IAnimationService
{
    void animate (JComponent comp, IPanelManager pm);
    void stopAnimate(JComponent jcomp);
    Optional<Integer> getMaxAnimationLayer(IPanelManager pm);
}
