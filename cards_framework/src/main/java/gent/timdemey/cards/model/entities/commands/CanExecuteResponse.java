package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.ExecutionState;

public class CanExecuteResponse
{
    private static final CanExecuteResponse YES = new CanExecuteResponse(ExecutionState.Yes, null); 
    private static final CanExecuteResponse YESPERM = new CanExecuteResponse(ExecutionState.YesPerm, null); 
    
    public final ExecutionState execState;
    public final String reason;
    
    private CanExecuteResponse(ExecutionState execState, String reason)
    {
        this.execState = execState;
        this.reason = reason;
    }
    
    public static CanExecuteResponse no(String reason)
    {
        if (reason == null || reason.isEmpty())
        {
            throw new IllegalArgumentException("reason must be specified");
        }
        return new CanExecuteResponse(ExecutionState.No, reason);
    }
    
    public static CanExecuteResponse error(String reason)
    {
        if (reason == null || reason.isEmpty())
        {
            throw new IllegalArgumentException("reason must be specified");
        }
        return new CanExecuteResponse(ExecutionState.Error, reason);
    }
    
    public static CanExecuteResponse yes()
    {
        return YES;
    }
    
    public static CanExecuteResponse yesPerm()
    {
        return YESPERM;
    }
    
    public boolean canExecute()
    {
        return execState == ExecutionState.Yes || execState == ExecutionState.YesPerm;
    }
}
