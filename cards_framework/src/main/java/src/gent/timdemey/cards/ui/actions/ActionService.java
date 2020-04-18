package gent.timdemey.cards.ui.actions;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.commands.C_LeaveGame;
import gent.timdemey.cards.model.entities.commands.C_Redo;
import gent.timdemey.cards.model.entities.commands.C_SetPlayer;
import gent.timdemey.cards.model.entities.commands.C_StartGame;
import gent.timdemey.cards.model.entities.commands.C_StopGame;
import gent.timdemey.cards.model.entities.commands.C_Undo;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.D_CreateGame;
import gent.timdemey.cards.model.entities.commands.D_JoinGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

public class ActionService implements IActionService
{

    private ReadOnlyState getReadOnlyState()
    {
        return Services.get(IContextService.class).getThreadContext().getReadOnlyState();
    }

    private boolean canExecute(CommandBase command)
    {
        return Services.get(IContextService.class).getThreadContext().canExecute(command);
    }

    private void execute(CommandBase command)
    {
        Services.get(IContextService.class).getContext(ContextType.UI).schedule(command);
    }

    @Override
    public boolean canExecuteAction(String id)
    {        
        switch (id)
        {
        case Actions.ACTION_UNDO:
            return canExecute(new C_Undo());
        case Actions.ACTION_REDO:
            return canExecute(new C_Redo());
        case Actions.ACTION_CREATE:
            return canExecute(new D_CreateGame());
        case Actions.ACTION_START:
            return canExecute(new C_StartGame((CardGame) null));
        case Actions.ACTION_STOP:
            return canExecute(new C_StopGame());
        case Actions.ACTION_QUIT:
            return true;
        case Actions.ACTION_DEBUG:
            return true;
        case Actions.ACTION_JOIN:
            return canExecute(new D_JoinGame());
        case Actions.ACTION_LEAVE:
            return canExecute(new C_LeaveGame());
        default:
            throw new UnsupportedOperationException("No such action id: " + id);
        }
    }

    @Override
    public void executeAction(String id)
    {
        switch (id)
        {
        case Actions.ACTION_UNDO:
            execute(new C_Undo());
            break;
        case Actions.ACTION_REDO:
            execute(new C_Redo());
            break;
        case Actions.ACTION_CREATE:
            execute(new D_CreateGame());
            break;
        case Actions.ACTION_JOIN:
            execute(new D_JoinGame());
            break;
        case Actions.ACTION_START:
            ICardGameCreationService creator = Services.get(ICardGameCreationService.class);
            List<List<Card>> cards = creator.getCards();
            
            IContextService contextServ = Services.get(IContextService.class);
            LimitedContext context = contextServ.getContext(ContextType.UI);

            C_SetPlayer cmdSetPlayer = new C_SetPlayer("DUMMY-PLAYER");
            context.schedule(cmdSetPlayer);
            
            List<UUID> playerIds = getReadOnlyState().getPlayers().getIds();
            List<PlayerConfiguration> playerConfigurations = creator.createStacks(playerIds, cards);
            CardGame cardGame = new CardGame(playerConfigurations);
            
            C_StartGame command = new C_StartGame(cardGame);            
            contextServ.getContext(ContextType.UI).schedule(command);
            break;
        case Actions.ACTION_STOP:
            execute(new C_StopGame());
            break;
        case Actions.ACTION_QUIT:
            System.exit(0);
            break;
        case Actions.ACTION_DEBUG:
            IGamePanelManager manager = Services.get(IGamePanelManager.class);
            manager.setDrawDebug(!manager.getDrawDebug());
            break;
        default:
            throw new UnsupportedOperationException("No such action id: " + id);
        }
    }
}
