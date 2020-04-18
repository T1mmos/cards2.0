package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.payload.P_OnMultiplayerGameStarted;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.CommandHistory;
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
        if (type == ContextType.Client)
        {
            forward(type, state);
            return;
        }
        
        if (type == ContextType.UI)
        {
            state.setCardGame(cardGame);
            state.setCommandHistory(new CommandHistory(true));
        }
        else
        {
            // broadcast to all players            
            String json_update = getCommandDtoMapper().toJson(this);
            state.getTcpConnectionPool().broadcast(state.getPlayers().getIds(), json_update);
        }
    }
}
