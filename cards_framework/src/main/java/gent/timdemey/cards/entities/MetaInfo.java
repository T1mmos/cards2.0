package gent.timdemey.cards.entities;

import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;

final class MetaInfo 
{
    public static class CompactConverter extends ASerializer<MetaInfo>
    {
        @Override
        protected void write(SerializationContext<MetaInfo> sc) {
            writeInt(sc, PROPERTY_MAJOR, sc.src.major);
            writeInt(sc, PROPERTY_MINOR, sc.src.minor);
            writeString(sc, PROPERTY_REQUESTINGPARTY, sc.src.requestingParty.toString());
        }

        @Override
        protected MetaInfo read(DeserializationContext dc) {
            int major = dc.obj.get(PROPERTY_MAJOR).getAsInt();
            int minor = dc.obj.get(PROPERTY_MINOR).getAsInt();
            UUID requestingPartyId = UUID.fromString(dc.obj.get(PROPERTY_REQUESTINGPARTY).getAsString());
            
            return new MetaInfo(major, minor, requestingPartyId);
        }        
    }
    
    final int major;
    final int minor;
    final UUID requestingParty;
    
    /**
     * Creates new meta info a top-level command.
     */
    MetaInfo ()
    {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        this.major = -1; //context.cardGameState.history.newCommandMajorId();
        this.minor = -1; // context.cardGameState.history.newCommandMinorId();
        this.requestingParty = context.getLocalId();
    }
    
    /**
     * If creating a subcommand within a composite command, use this constructor and supply the already known major counter.
     * @param major
     */
    MetaInfo (int major)
    {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        this.major = major;
        this.minor = context.getCardGameState().history.newCommandMinorId();
        this.requestingParty = context.getLocalId();
    }
    
    MetaInfo (int major, int minor, UUID requestingParty)
    {
        Preconditions.checkNotNull(requestingParty);
        
        this.major = major;
        this.minor = minor;
        this.requestingParty = requestingParty;
    }
}
