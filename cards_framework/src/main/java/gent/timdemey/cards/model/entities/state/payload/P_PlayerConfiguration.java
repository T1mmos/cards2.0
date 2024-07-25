package gent.timdemey.cards.model.entities.state.payload;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_PlayerConfiguration extends PayloadBase
{
    public UUID playerId;
    public List<CardStack> cardStacks;
}
