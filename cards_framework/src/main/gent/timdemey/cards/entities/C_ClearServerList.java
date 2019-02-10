package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;

class C_ClearServerList extends ACommandPill{

    C_ClearServerList(MetaInfo info) {
        super(info);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        Services.get(IContextProvider.class).getThreadContext().getCardGameState().servers.clear();
    }

    @Override
    public void visitExecuted(IGameEventListener listener) 
    {
        // nothing ?
    }
}
