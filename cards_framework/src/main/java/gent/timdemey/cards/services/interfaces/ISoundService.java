package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;

public interface ISoundService 
{
    void play(ResourceDescriptor desc); 

    default void playCardPlacement(ReadOnlyCardStack destination)
    {
        play(ResourceDescriptors.SoundPutDown);
    }

    default void playGameOutcome(boolean won)
    {
    }

    void setMuted(boolean muted);

    boolean isMuted();
}
