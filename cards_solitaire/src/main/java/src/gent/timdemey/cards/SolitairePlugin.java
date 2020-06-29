package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.cardgame.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.commands.SolitaireCommandService;
import gent.timdemey.cards.services.gamepanel.SolitaireGamePanelService;
import gent.timdemey.cards.services.gamepanel.SolitairePositionService;
import gent.timdemey.cards.services.id.SolitaireIdService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;

public class SolitairePlugin implements ICardPlugin
{

    @Override
    public void installServices()
    {
        App.getServices().install(ICommandService.class, new SolitaireCommandService());
        App.getServices().install(ICardGameService.class, new SolitaireCardGameCreationService());
        App.getServices().install(IIdService.class, new SolitaireIdService());
    }

    @Override
    public void installUiServices()
    {
        App.getServices().install(IPositionService.class, new SolitairePositionService());
        App.getServices().install(IGamePanelService.class, new SolitaireGamePanelService());
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
