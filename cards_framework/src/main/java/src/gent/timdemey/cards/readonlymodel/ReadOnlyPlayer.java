package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.game.Player;

public class ReadOnlyPlayer extends ReadOnlyEntityBase<Player>
{
    ReadOnlyPlayer(Player player)
    {
        super(player);
    }

    public String getName()
    {
        return entity.name;
    }
    
    
}
