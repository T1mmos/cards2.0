package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_LeaveLobby;
import gent.timdemey.cards.model.entities.commands.C_Redo;
import gent.timdemey.cards.model.entities.commands.C_StartLocalGame;
import gent.timdemey.cards.model.entities.commands.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.C_StopGame;
import gent.timdemey.cards.model.entities.commands.C_Undo;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.D_Connect;
import gent.timdemey.cards.model.entities.commands.D_StartServer;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.context.ContextType;

public class ActionService implements IActionService
{
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
        case Actions.ACTION_CREATE_MULTIPLAYER:
            return canExecute(new D_StartServer());
        case Actions.ACTION_START:
            return canExecute(new C_StartLocalGame());
        case Actions.ACTION_STARTMULTIPLAYER:
            return canExecute(new C_StartMultiplayerGame());
        case Actions.ACTION_STOP:
            return canExecute(new C_StopGame());
        case Actions.ACTION_QUIT:
            return true;
        case Actions.ACTION_DEBUG:
            return true;
        case Actions.ACTION_JOIN:
            return canExecute(new D_Connect());
        case Actions.ACTION_LEAVE:
            return canExecute(new C_LeaveLobby());
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
        case Actions.ACTION_CREATE_MULTIPLAYER:
            execute(new D_StartServer());
            break;
        case Actions.ACTION_JOIN:
            execute(new D_Connect());
            break;
        case Actions.ACTION_START:       
            execute(new C_StartLocalGame());
            break;
        case Actions.ACTION_STARTMULTIPLAYER:
            execute(new C_StartMultiplayerGame());
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
