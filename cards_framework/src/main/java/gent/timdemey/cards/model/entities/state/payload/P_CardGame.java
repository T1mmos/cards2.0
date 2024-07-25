package gent.timdemey.cards.model.entities.state.payload;

import java.util.List;

import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_CardGame extends PayloadBase
{
    public List<PlayerConfiguration> playerConfigurations;
}
