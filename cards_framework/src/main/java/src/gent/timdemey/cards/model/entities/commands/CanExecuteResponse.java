package gent.timdemey.cards.model.entities.commands;

class CanExecuteResponse
{
    private static final CanExecuteResponse YES = new CanExecuteResponse(true, null); 
    
    final boolean canExecute;
    final String reason;
    
    private CanExecuteResponse(boolean canExecute, String reason)
    {
        this.canExecute = canExecute;
        this.reason = reason;
    }
    
    static CanExecuteResponse no(String reason)
    {
        if (reason == null || reason.isEmpty())
        {
            throw new IllegalArgumentException("reason must be specified");
        }
        return new CanExecuteResponse(false, reason);
    }
    
    static CanExecuteResponse yes()
    {
        return YES;
    }
}
