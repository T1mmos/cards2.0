package gent.timdemey.cards.services.contract;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

public class GetCardStackScaleInfoRequest extends GetScaleInfoRequest
{
    public final ReadOnlyCardStack cardStack;
    
    public GetCardStackScaleInfoRequest(ReadOnlyCardStack cardStack)
    {
        this.cardStack = cardStack;
    }
}
