package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.commands.C_CreateServer;
import gent.timdemey.cards.multiplayer.ConnectInfo;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.IGameOperationsService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.CreateMultiplayerGameDialogContent;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameDialogContent;

public class ActionService implements IActionService {

    @Override
    public boolean canExecuteAction(String id) {
        switch (id)
        {
        case AAction.ACTION_UNDO:
            return Services.get(IGameOperationsService.class).canUndo();
        case AAction.ACTION_REDO:
            return Services.get(IGameOperationsService.class).canRedo();
        case AAction.ACTION_CREATE:
            return Services.get(IGameOperationsService.class).canCreateGame();
        case AAction.ACTION_START:
            return Services.get(IGameOperationsService.class).canStartGame();     
        case AAction.ACTION_STOP:
            return Services.get(IGameOperationsService.class).canStopGame();
        case AAction.ACTION_QUIT:
            return true;
        case AAction.ACTION_DEBUG:
            return true;
        case AAction.ACTION_JOIN:
            return true;
        case AAction.ACTION_LEAVE:
            return true;
        default:
            throw new UnsupportedOperationException("No such action id: " + id);
        }
    }
    
    @Override
    public void executeAction(String id) {
        switch (id)
        {
        case AAction.ACTION_UNDO:
            Services.get(IGameOperationsService.class).undo();
            break;
        case AAction.ACTION_REDO:
            Services.get(IGameOperationsService.class).redo();
            break;
        case AAction.ACTION_CREATE:
        {
            CreateMultiplayerGameDialogContent content = new CreateMultiplayerGameDialogContent();
            DialogData<C_CreateServer> data = Services.get(IDialogService.class).ShowAdvanced(Loc.get("dialog_title_creategame"), null, content, DialogButtonType.BUTTONS_OK_CANCEL);
            
            if (data.closeType == DialogButtonType.Ok)
            {
                Services.get(IGameOperationsService.class).createGame(data.payload);
            }
            break;
        }            
        case AAction.ACTION_JOIN:
        {
            JoinMultiplayerGameDialogContent content = new JoinMultiplayerGameDialogContent();
            DialogData<ConnectInfo> data = Services.get(IDialogService.class).ShowAdvanced(Loc.get("dialog_title_joingame"), null, content, DialogButtonType.BUTTONS_OK_CANCEL);
            
            if (data.closeType == DialogButtonType.Ok)
            {
                Services.get(IGameOperationsService.class).joinGame(data.payload.serverInfo.address, data.payload.serverInfo.tcpport, data.payload.playerName);
            }
            break;
        }            
        case AAction.ACTION_START:
            Services.get(IGameOperationsService.class).startGame();        
            break;
        case AAction.ACTION_STOP:
            Services.get(IGameOperationsService.class).stopGame();
            break;
        case AAction.ACTION_QUIT:
            System.exit(0);
            break;
        case AAction.ACTION_DEBUG:
            IGamePanelManager manager = Services.get(IGamePanelManager.class);            
            manager.setDrawDebug(!manager.getDrawDebug());
            break;
        default:
            throw new UnsupportedOperationException("No such action id: " + id);
        }
    }
}
