package gent.timdemey.cards.model.entities.cards.payload;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_PlayerConfiguration extends PayloadBase
{
    public UUID playerId;
    public List<CardStack> cardStacks;
}
