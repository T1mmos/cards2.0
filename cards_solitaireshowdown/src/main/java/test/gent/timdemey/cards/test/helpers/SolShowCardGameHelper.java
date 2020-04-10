package gent.timdemey.cards.test.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.cards.payload.P_Card;
import gent.timdemey.cards.model.entities.cards.payload.P_CardGame;
import gent.timdemey.cards.model.entities.cards.payload.P_CardStack;
import gent.timdemey.cards.model.entities.cards.payload.P_PlayerConfiguration;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.services.boot.SolShowCardGameCreationService;

public class SolShowCardGameHelper
{    
    public static CardGame createFixedSolShowCardGame(Player player1, Player player2)
    {        
        Card[] fixedShuffledDeck1 = CardDeckHelper.createFixedDeck1();
        Card[] fixedShuffledDeck2 = CardDeckHelper.createFixedDeck2();
                
        List<UUID> playerIds = Arrays.asList(player1.id, player2.id);
        List<Card> cards1 = Arrays.asList(fixedShuffledDeck1);
        List<Card> cards2 = Arrays.asList(fixedShuffledDeck2);
        List<List<Card>> allCards = Arrays.asList(cards1, cards2);
        
        SolShowCardGameCreationService serv = new SolShowCardGameCreationService();
        List<PlayerConfiguration> playerConfigs = serv.createStacks(playerIds, allCards);
        
        // because the id's of the entities are randomly chosen, we will deep-copy all
        // entities and use fixed id's
        List<PlayerConfiguration> playerConfigsFixed = new ArrayList<>();
        for (int pcNr = 0; pcNr < playerConfigs.size(); pcNr++)
        {
            PlayerConfiguration pc = playerConfigs.get(pcNr);
            List<CardStack> cardStacksFixed = new ArrayList<>();
            for (int csNr = 0; csNr < pc.cardStacks.size(); csNr++)
            {
                CardStack cs = pc.cardStacks.get(csNr);
                List<Card> cardsFixed = new ArrayList<>();
                for (int cNr = 0; cNr < cs.cards.size(); cNr++)
                {
                    Card card = cs.cards.get(cNr);
                    
                    P_Card cPl = new P_Card();
                    {
                        cPl.id = SolShowTestIds.getFixedCardId(pcNr, cs.cardStackType, cs.typeNumber, cNr);
                        cPl.suit = card.suit;
                        cPl.value = card.value;
                        cPl.visible = card.visibleRef.get();
                    }
                    Card cardFixed = new Card(cPl);
                    cardsFixed.add(cardFixed);
                }
                
                UUID id = SolShowTestIds.getFixedCardStackId(pcNr, cs.cardStackType, cs.typeNumber);
                P_CardStack csPl = new P_CardStack();
                {
                    csPl.id = id;
                    csPl.cardStackType = cs.cardStackType;
                    csPl.typeNumber = cs.typeNumber;
                    csPl.cards = cardsFixed;
                }
                CardStack cardStackFixed = new CardStack(csPl);
                for (Card c : cardStackFixed.cards)
                {
                    c.cardStack = cardStackFixed;
                }
                
                cardStacksFixed.add(cardStackFixed);
            }
            
            P_PlayerConfiguration pcPl = new P_PlayerConfiguration();
            {
                pcPl.id = IdHelper.createFixedPlayerConfigurationId(pcNr);
                pcPl.playerId = pc.playerId;
                pcPl.cardStacks = cardStacksFixed;
            }
            PlayerConfiguration pcFixed = new PlayerConfiguration(pcPl);
            playerConfigsFixed.add(pcFixed);
        }
        
        P_CardGame pl = new P_CardGame();
        {
            pl.id = IdHelper.createFixedCardGameId();
            pl.playerConfigurations = playerConfigsFixed;
        }
        
        return new CardGame(pl);
    }
}
