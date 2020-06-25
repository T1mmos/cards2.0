package gent.timdemey.cards.services.scaleman;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.scaleman.comps.CardStackScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class SolitaireScalableComponentService extends ScalableComponentService
{

    @Override
    public IScalableComponent getOrCreateScalableComponent(ReadOnlyCardStack cardstack)
    {
        IIdService uuidServ = Services.get(IIdService.class);

        UUID compId = model2comp.get(cardstack.getId());
        if (compId == null)
        {
            // get the ids
            compId = uuidServ.createCardStackComponentId(cardstack);
        }

        CardStackScalableImageComponent comp = (CardStackScalableImageComponent) components.get(compId);
        if (comp == null)
        {
            UUID csResId = uuidServ.createCardStackResourceId(cardstack.getCardStackType());

            // create the component using its necessary image resources
            ScalableImageResource res = (ScalableImageResource) getResourceOrThrow(csResId);
            comp = new CardStackScalableImageComponent(compId, cardstack, res);

            components.put(compId, comp);
        }

        return comp;
    } 

}
