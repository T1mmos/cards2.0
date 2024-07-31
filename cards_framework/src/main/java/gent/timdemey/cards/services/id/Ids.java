/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.services.id;

import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

/**
 *
 * @author Timmos
 */
public class Ids
{    
    public static final IdP0 RESID_CARD_BACKSIDE = new IdP0("resid.card.backside");
    public static final IdP2_2<CardSuit, CardValue> RESID_CARD_FRONTSIDE_SV = new IdP2_2<>( "resid.card.frontside.%s%s", suit -> suit.getTextual(), value -> value.getTextual());    
    public static final IdWrapperP2<ReadOnlyCard, CardSuit, CardValue> RESID_CARD_FRONTSIDE = new IdWrapperP2<>(RESID_CARD_FRONTSIDE_SV, card -> card.getSuit(), card -> card.getValue());
    
    
    
    public static final IdP1<String> RESID_CARDSTACK_TYPE = new IdP1<>("resid.cardstack.%s", cardStackType -> cardStackType);
    public static final IdWrapperP1<ReadOnlyCardStack, String> RESID_CARDSTACK = new IdWrapperP1<>(RESID_CARDSTACK_TYPE, cardStack -> cardStack.getCardStackType());
    
    public static final IdP1<String> RESID_FONT = new IdP1<>("resid.font.%s", fontname -> fontname);
    public static final IdP1<ReadOnlyCard> COMPID_CARD = new IdP1<>("compid.card.%s", card -> card.getId().toString());
    public static final IdP1<ReadOnlyCardStack> COMPID_CARDSTACK = new IdP1<>("compid.cardstack.%s", cardStack -> cardStack.getId().toString());    
    
}
