package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_OnGameEnded extends CommandBase
{
    public final UUID winnerId;
    private final Loc _Loc;
    private final IFrameService _FrameService;

    public C_OnGameEnded(
        Container container, IFrameService frameService, Loc loc,
        P_OnGameEnded parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._Loc = loc;
        
        this.winnerId = parameters.winnerId;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);

        _State.setGameState(GameState.Ended);
       
        String title, msg;
        if (_State.id.equals(winnerId))
        {
            title = _Loc.get(LocKey.DialogTitle_youwin);
            msg = _Loc.get(LocKey.DialogMessage_youwin);    
        }
        else
        {
            title = _Loc.get(LocKey.DialogTitle_youlose);
            msg = _Loc.get(LocKey.DialogMessage_youlose);
        }
        
        _FrameService.showMessage(title, msg);
    }
}
