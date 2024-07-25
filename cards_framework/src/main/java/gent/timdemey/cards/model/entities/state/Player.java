package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateValueRef;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

public class Player extends EntityBase
{
    public static final Property<String> Name = Property.of(Player.class, String.class, "Name");
    public static final Property<Integer> Score = Property.of(Player.class, Integer.class, "Score");

    private StateValueRef<String> nameRef;
    private StateValueRef<Integer> scoreRef;

    Player(IChangeTracker changeTracker, UUID id, String name)
    {
        super(id);
        this.nameRef = new StateValueRef<>(changeTracker, Name, id, name);
        this.scoreRef = new StateValueRef<>(changeTracker, Score, id, 0);
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
