package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_DenyClient extends CommandBase
{
    private final IFrameService _FrameService;
    
    public C_DenyClient(
        Container container, IFrameService frameService,         
        P_DenyClient parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
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
        if (_ContextType == ContextType.UI)
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
