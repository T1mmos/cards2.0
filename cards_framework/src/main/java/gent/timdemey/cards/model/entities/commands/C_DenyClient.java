package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_DenyClient extends CommandBase
{

    private final IFrameService _FrameService;
    
    public C_DenyClient(IContextService contextService, IFrameService frameService, P_DenyClient parameters)
    {
        super(contextService, parameters);
        
        this._FrameService = frameService;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            _FrameService.showMessage("test",
                    "TEST: You were denied to join the game. Maybe the server is full.");
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
