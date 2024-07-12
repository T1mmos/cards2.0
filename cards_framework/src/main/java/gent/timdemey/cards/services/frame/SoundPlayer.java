package gent.timdemey.cards.services.frame;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.ISoundService;

public class SoundPlayer
{
    private final ReadOnlyChange change;
    private ReadOnlyState state;
    private final ISoundService soundServ;
    
    SoundPlayer(ReadOnlyChange change, ReadOnlyState state)
    {
        this.change = change;
        this.state = state;
        this.soundServ = Services.get(ISoundService.class);
    }
    
    void playAll()
    {
        CheckCardGameStart();
        CheckCardPutDown();
    }

    private void CheckCardGameStart()
    {
        if (change.property == ReadOnlyState.CardGame)
        {
            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame != null)
            {
                soundServ.play(ResourceDescriptors.SoundTest);
            }
        }
    }
    
    private void CheckCardPutDown()
    {
        if (change.property == ReadOnlyCardStack.Cards)
        {
            if (change.addedValues != null && !change.addedValues.isEmpty())
            {
                soundServ.play(ResourceDescriptors.SoundPutDown);
            }
        }
    }
}
