package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_OnMultiplayerGameStarted extends PayloadBase
{
    public CardGame cardGame;
}
