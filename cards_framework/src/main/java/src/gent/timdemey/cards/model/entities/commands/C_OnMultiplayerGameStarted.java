package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.payload.P_OnMultiplayerGameStarted;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnMultiplayerGameStarted extends CommandBase
{
    public final CardGame cardGame;
    
    public C_OnMultiplayerGameStarted(CardGame cardGame)
    {
        this.cardGame = cardGame;
    }
    
    public C_OnMultiplayerGameStarted(P_OnMultiplayerGameStarted pl)
    {
        super(pl);
        this.cardGame = pl.cardGame;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;        
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {        
        if (type == ContextType.UI)
        {
            state.setCardGame(cardGame);
            state.setGameState(GameState.Started);
            state.setCommandHistory(new CommandHistory(true));
        }
        else
        {
            // broadcast to all players            
            INetworkService netServ = Services.get(INetworkService.class);
            netServ.broadcast(state.getLocalId(), state.getPlayers().getIds(), this, state.getTcpConnectionPool());            
        }
    }
}
