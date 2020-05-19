package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.game.Player;

public class ReadOnlyPlayer extends ReadOnlyEntityBase<Player>
{
    public static ReadOnlyProperty<Integer> Score = ReadOnlyProperty.of(Player.Score);
    
    ReadOnlyPlayer(Player player)
    {
        super(player);
    }

    public String getName()
    {
        return entity.getName();
    }
    
    public int getScore()
    {
        return entity.getScore();
    }
}
