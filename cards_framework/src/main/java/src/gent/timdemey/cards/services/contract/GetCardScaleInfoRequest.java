package gent.timdemey.cards.services.contract;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;

public class GetCardScaleInfoRequest extends GetScaleInfoRequest
{
    public final ReadOnlyCard card;
    
    public GetCardScaleInfoRequest (ReadOnlyCard card)
    {
        this.card = card;
    }
}
