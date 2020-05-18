package gent.timdemey.cards.model.entities.game;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateValueRef;
import gent.timdemey.cards.utils.Debug;

public class Player extends EntityBase
{
    public static final Property<String> Name = Property.of(Player.class, String.class, "Name");
    public static final Property<Integer> Score = Property.of(Player.class, Integer.class, "Score");

    private StateValueRef<String> nameRef;
    private StateValueRef<Integer> scoreRef;

    public Player(String name)
    {
        super();
        this.nameRef = new StateValueRef<>(Name, id, name);
        this.scoreRef = new StateValueRef<>(Score, id, 0);
    }

    public Player(P_Player pl)
    {
        super(pl);
        this.nameRef = new StateValueRef<>(Name, id, pl.name);
        this.scoreRef = new StateValueRef<>(Score, id, 0);
    }

    public void setName (String name)
    {
        this.nameRef.set(name);
    }
    
    public String getName()
    {
        return this.nameRef.get();
    }
    
    public void setScore(int score)
    {
        this.scoreRef.set(score);
    }
    
    public void addScore(int score)
    {
        this.scoreRef.set(this.scoreRef.get() + score);
    }
    
    public int getScore()
    {
        return this.scoreRef.get();
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("name", nameRef.get()) + 
                Debug.getKeyValue("score", scoreRef.get());
    }
}
