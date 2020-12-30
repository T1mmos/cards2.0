package gent.timdemey.cards.test.common;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.SolShowPlugin;

public class SolShowTestBase extends TestBase
{
    public static void installSolShowCardPlugin()
    {
        App.getServices().installIfAbsent(ICardPlugin.class, () -> new SolShowPlugin());
    }
}
