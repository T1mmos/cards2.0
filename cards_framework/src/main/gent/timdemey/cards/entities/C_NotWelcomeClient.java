package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.dialogs.IDialogService;

class C_NotWelcomeClient extends ACommandPill {

    static class CompactConverter extends ACommandSerializer<C_NotWelcomeClient>
    {
        @Override
        protected void writeCommand(SerializationContext<C_NotWelcomeClient> sc) {
            writeString(sc, PROPERTY_SERVER_MESSAGE, sc.src.serverMessage);
        }

        @Override
        protected C_NotWelcomeClient readCommand(DeserializationContext dc, MetaInfo metaInfo) {
            String serverMessage = readString(dc, PROPERTY_SERVER_MESSAGE);
            
            return new C_NotWelcomeClient(metaInfo, serverMessage);
        }        
    }
    
    String serverMessage;
    
    C_NotWelcomeClient(MetaInfo info, String serverMessage) {
        super(info);
        this.serverMessage = serverMessage;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() 
    {
        if (getContextType() == ContextType.Client)
        {
            scheduleOn(ContextType.UI);
        }
        else if (getContextType() == ContextType.UI)
        {
            Services.get(IDialogService.class).ShowMessage("test", "not welcome: "+serverMessage);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }

}
