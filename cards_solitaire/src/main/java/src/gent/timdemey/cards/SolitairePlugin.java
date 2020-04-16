package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.boot.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.commands.SolitaireCommandService;
import gent.timdemey.cards.services.gamepanel.SolitaireGamePanelManager;
import gent.timdemey.cards.services.position.SolitairePositionManager;

public class SolitairePlugin implements ICardPlugin
{

    @Override
    public void installServices()
    {
        App.services.install(ICommandService.class, new SolitaireCommandService());
        App.services.install(ICardGameCreationService.class, new SolitaireCardGameCreationService());
    }
    


    @Override
    public void installUiServices()
    {
        App.services.install(IPositionManager.class, new SolitairePositionManager());
        App.services.install(IGamePanelManager.class, new SolitaireGamePanelManager());
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
