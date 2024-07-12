package gent.timdemey.cards.model.entities.cards.payload;

import java.util.List;

import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_CardGame extends PayloadBase
{
    public List<PlayerConfiguration> playerConfigurations;
}
