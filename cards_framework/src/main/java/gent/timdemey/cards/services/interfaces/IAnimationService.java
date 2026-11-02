package gent.timdemey.cards.services.interfaces;

import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.services.animation.AnimationKind;
import gent.timdemey.cards.ui.panels.IPanelManager;

public interface IAnimationService
{
    void animate (List<? extends JComponent> comps, IPanelManager pm, AnimationKind kind);
    void stopAnimate(JComponent jcomp);
}
