package gent.timdemey.cards.services.scaleman;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.scaleman.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class SolitaireScalableComponentService extends ScalableComponentService
{

    @Override
    public IScalableComponent getOrCreateScalableComponent(ReadOnlyCardStack card)
    {
        IIdService uuidServ = Services.get(IIdService.class);

        UUID compId = model2comp.get(card.getId());
        if (compId == null)
        {
            // get the ids
            compId = uuidServ.createCardComponentId(card);
        }

        CardScalableImageComponent comp = (CardScalableImageComponent) components.get(compId);
        if (comp == null)
        {
            UUID resFrontId = uuidServ.createCardFrontResourceId(card.getSuit(), card.getValue());
            UUID resBackId = uuidServ.createCardBackResourceId();

            // create the component using its necessary image resources
            ScalableImageResource res_front = (ScalableImageResource) getResourceOrThrow(resFrontId);
            ScalableImageResource res_back = (ScalableImageResource) getResourceOrThrow(resBackId);
            comp = new CardScalableImageComponent(compId, card, res_front, res_back);

            // initialize the card to show its front or back
            comp.updateVisible();

            components.put(compId, comp);
        }

        return comp;
    } 

}
