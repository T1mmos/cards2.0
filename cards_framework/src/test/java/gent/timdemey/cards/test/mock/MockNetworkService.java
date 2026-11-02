package gent.timdemey.cards.test.mock;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.interfaces.INetworkService;

public class MockNetworkService implements INetworkService
{

    @Override
    public void sendMove(State state, CommandBase command)
    {        
        Logger.debug("MockNetworkService: sendMove()");
    }

}
