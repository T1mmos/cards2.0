package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;

public class AGameCommandOperations implements IGameCommandOperations {
    
    @Override
    public boolean canStartGame(C_StartGame command) {
        return Services.get(IGameOperations.class).canStartGame();
    }

    @Override
    public boolean canStopGame(C_StopGame command) {
        return Services.get(IGameOperations.class).canStopGame();
    }
    
    @Override
    public boolean canMove(C_Move command) {
        E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
        
        E_CardStack srcCardStack = cardGame.getCardStack(command.srcCardStackId);
        E_CardStack dstCardStack = cardGame.getCardStack(command.dstCardStackId);
        E_Card card = cardGame.getCard(command.cardId);
        
        return Services.get(IGameOperations.class).canMove(srcCardStack, dstCardStack, card);
    }
}
