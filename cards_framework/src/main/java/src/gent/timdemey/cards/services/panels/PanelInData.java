package gent.timdemey.cards.services.panels;

import java.util.function.Consumer;

public class PanelInData<IN>
{
    public IN data_in;
    public Consumer<PanelButtonType> verifyButtonFunc;
    public Runnable closeFunc;
}
