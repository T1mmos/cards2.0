package gent.timdemey.cards.model.entities.state.payload;

import java.util.List;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;

public class P_CardGame extends PayloadBase
{
    public List<PlayerConfiguration> playerConfigurations;
}
