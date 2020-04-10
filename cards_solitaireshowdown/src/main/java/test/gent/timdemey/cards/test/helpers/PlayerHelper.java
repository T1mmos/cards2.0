package gent.timdemey.cards.test.helpers;

import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;

public class PlayerHelper
{
    private static final String[] NAMES = new String [] 
    {
        "Tim",
        "Jozefien"
    };
    
    public static Player getFixedPlayer(int nr)
    {
        P_Player pl = new P_Player();
        {
            pl.id = IdHelper.createFixedPlayerId(nr);
            pl.name = NAMES[nr];
        }
        Player player = new Player(pl);        
        return player;
    }
}
