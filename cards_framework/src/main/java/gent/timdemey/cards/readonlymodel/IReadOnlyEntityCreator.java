package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.EntityBase;

public interface IReadOnlyEntityCreator<S extends EntityBase, T extends ReadOnlyEntityBase>
{
    T CreateReadOnlyEntity(S src);
}
