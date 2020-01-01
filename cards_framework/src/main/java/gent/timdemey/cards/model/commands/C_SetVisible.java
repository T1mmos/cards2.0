package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.readonlymodel.ACommand;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;

public class C_SetVisible extends CommandBase
{
    private final UUID playerId;
    private final List<ReadOnlyCard> cards;
    private final boolean visible;
    
    C_SetVisible (UUID playerId, List<ReadOnlyCard> cards, boolean visible)
    {
        Preconditions.checkNotNull(cards);
        for (ReadOnlyCard card : cards)
        {
            Preconditions.checkNotNull(card);
            Preconditions.checkArgument(card.isVisible() != visible);
        }        
        
        this.playerId = playerId;
        this.cards = cards;
        this.visible = visible;
    }
    
    @Override
    public CommandType getCommandType()
    {
        return CommandType.Gameplay;
    } 

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        for (ReadOnlyCard card : cards)
        {
            card.setVisible(visible);
        } 
    }

    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public void undo() {
        for (ReadOnlyCard card : cards)
        {
            card.setVisible(!visible);
        } 
    }
}
