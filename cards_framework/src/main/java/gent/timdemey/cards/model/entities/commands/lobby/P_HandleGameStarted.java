package gent.timdemey.cards.model.entities.commands.lobby;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.state.CardGame;

/**
 *
 * @author Timmos
 */
public class P_HandleGameStarted extends CommandPayloadBase
{
    public CardGame cardGame;    
}
