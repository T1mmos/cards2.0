package gent.timdemey.cards.common;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.services.cardgame.SolShowTestCardGameService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.test.common.TestBase;

public class SolShowTestBase extends TestBase
{
    public static void installSolShowCardPlugin()
    {
        App.getServices().installIfAbsent(ICardPlugin.class, () -> new SolShowPlugin());
        App.getServices().installIfAbsent(ICardGameService.class, () -> new SolShowTestCardGameService());
    }
}
