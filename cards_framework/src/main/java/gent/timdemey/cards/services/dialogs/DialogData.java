package gent.timdemey.cards.services.dialogs;

public class DialogData<OUT>
{

    public final DialogButtonType closeType;
    public final OUT payload;

    public DialogData(DialogButtonType closeType, OUT payload)
    {
        this.closeType = closeType;
        this.payload = payload;
    }
}
