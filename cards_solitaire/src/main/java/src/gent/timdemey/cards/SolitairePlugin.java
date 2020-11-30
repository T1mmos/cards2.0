package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.cardgame.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.commands.SolitaireCommandService;
import gent.timdemey.cards.services.id.SolitaireIdService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.panels.SolitairePanelService;
import gent.timdemey.cards.services.panels.SolitairePositionService;

public class SolitairePlugin implements ICardPlugin
{
    @Override
    public void installServices(Services services)
    {        
        services.installIfAbsent(ICommandService.class, () -> new SolitaireCommandService());
        services.installIfAbsent(ICardGameService.class, () -> new SolitaireCardGameCreationService());
        services.installIfAbsent(IIdService.class, () -> new SolitaireIdService());
    }

    @Override
    public void installUiServices(Services services)
    {
        App.getServices().installIfAbsent(IPositionService.class, () -> new SolitairePositionService());
        App.getServices().installIfAbsent(IPanelService.class, () -> new SolitairePanelService());
    }

    @Override
    public String getName()
    {
        return "Solitaire";
    }

    @Override
    public int getMajorVersion()
    {
        return 0;
    }

    @Override
    public int getMinorVersion()
    {
        return 6;
    }

    @Override
    public int getPlayerCount()
    {
        return 1;
    }

    @Override
    public State createState()
    {
        return new State();
    }
}
