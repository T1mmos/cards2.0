package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

public interface IDialogService {
    public void ShowNotImplemented();
    
    public DialogData<Void> ShowMessage(String title, String message);
    
    public DialogData<Void> ShowInternalError();
    
    public <IN,OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN,OUT> dialogContent, DialogButtonType closeType);
    
    public <IN,OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN,OUT> dialogContent, EnumSet<DialogButtonType> closeTypes);
}
