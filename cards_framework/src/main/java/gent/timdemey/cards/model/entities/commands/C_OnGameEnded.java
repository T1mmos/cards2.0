package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnGameEnded;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_OnGameEnded extends CommandBase
{
    public final UUID winnerId;
    private final Loc _Loc;
    private final IFrameService _FrameService;

    public C_OnGameEnded(IContextService contextService, CommandFactory commandFactory, IFrameService frameService, Loc loc, P_OnGameEnded parameters)
    {
        super(contextService, parameters);
        
        this._FrameService = frameService;
        this._Loc = loc;
        
        this.winnerId = parameters.winnerId;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        state.setGameState(GameState.Ended);
       
        String title, msg;
        if (state.getLocalId().equals(winnerId))
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
