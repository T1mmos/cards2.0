package gent.timdemey.cards.model.entities.commands;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.entities.commands.save.SaveFunctions;
import gent.timdemey.cards.model.entities.commands.save.SaveProperty;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SaveState extends CommandBase
{
    private final P_SaveState payload;
    
    private static final List<SaveProperty> PROPERTIES = Arrays.asList(

        new SaveProperty(SaveFunctions::canExec_PlayerName, SaveFunctions::exec_PlayerName),
        new SaveProperty(SaveFunctions::canExec_ServerTcpPort, SaveFunctions::exec_ServerTcpPort),
        new SaveProperty(SaveFunctions::canExec_ServerUdpPort, SaveFunctions::exec_ServerUdpPort)
    ); 
    
    public C_SaveState(P_SaveState payload)
    {
        super(payload);
        
        this.payload = payload;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        for (SaveProperty saveProp : PROPERTIES)
        {
            boolean canExecProp = saveProp.canExecFunc.apply(state, payload);
            if (canExecProp)
            {
                return CanExecuteResponse.yesPerm();
            }
        }
       
        return CanExecuteResponse.no("No (valid) changes were detected in the settings");
    }
    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        for (SaveProperty saveProp : PROPERTIES)
        {
            saveProp.execFunc.accept(state, payload);
        }
    }
}
