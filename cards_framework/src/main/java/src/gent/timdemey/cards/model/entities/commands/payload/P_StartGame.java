package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_StartGame extends PayloadBase
{
    public CardGame cardGame;
}
