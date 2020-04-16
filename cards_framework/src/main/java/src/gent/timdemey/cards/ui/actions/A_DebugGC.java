package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class A_DebugGC extends AbstractAction
{
    protected A_DebugGC()
    {
        super(Loc.get(LocKey.DebugMenu_gc));
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.gc();
    }

}
