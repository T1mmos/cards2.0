/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.services.id;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;

/**
 *
 * @author Timmos
 */
public class SolShowIds extends Ids
{
    public static final IdP1<ReadOnlyCardStack> COMPID_SPECIALCOUNTER = new IdP1<>("compid.specialcounter.%s", cardStack -> cardStack.getId().toString());
    public static final IdP1<Boolean> RESID_SPECIALBACKGROUND = new IdP1<>("resid.specialcounter.%s", remote -> remote.toString());
    public static final IdP1<ReadOnlyCardStack> COMPID_SPECIALBACKGROUND = new IdP1<>("compid.specialbackground.%s", cardStack -> cardStack.getId().toString());
    public static final IdP1<ReadOnlyPlayer> COMPID_PLAYERNAME = new IdP1<>("compid.playername.%s", player -> player.getId().toString());    
    public static final IdP1<Boolean> RESID_PLAYERNAME_BACKGROUND = new IdP1<>("resid.playername.background.%s", remote -> remote.toString());
    public static final IdP1<Boolean>  RESID_CARDAREA_BACKGROUND = new IdP1<>("resid.cardarea.background.%s", remote -> remote.toString());    
    public static final IdP1<Boolean> COMPID_PLAYERNAME_BACKGROUND = new IdP1<>("compid.playername.background.%s", remote -> remote.toString());
    public static final IdP1<Boolean> COMPID_CARDAREA_BACKGROUND = new IdP1<>("compid.cardarea.background.%s", remote -> remote.toString());
    public static final IdP0 COMPID_VS = new IdP0("compid.vs");    
    public static final IdP0 RESID_VS = new IdP0("resid.vs");
}
