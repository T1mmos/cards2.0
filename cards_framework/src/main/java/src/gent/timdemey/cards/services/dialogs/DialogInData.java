package gent.timdemey.cards.services.dialogs;

import java.util.function.Consumer;

public class DialogInData<IN>
{
    public IN data_in;
    public Consumer<DialogButtonType> verifyButtonFunc;
    public Runnable closeFunc;
}
