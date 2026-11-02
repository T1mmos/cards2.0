package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;

public interface ISoundService 
{
    void play(ResourceDescriptor desc); 

    void setMuted(boolean muted);

    boolean isMuted();
}
