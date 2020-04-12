package gent.timdemey.cards.test.common;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowPlugin;

public class SolShowTestUtils
{
    public static void installSolShowCardPlugin()
    {
        Services.install(ICardPlugin.class, new SolShowPlugin());
    }
}
