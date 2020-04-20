package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.common.EntityBase;

public interface IReadOnlyEntityCreator<S extends EntityBase, T extends ReadOnlyEntityBase<S>>
{
    T CreateReadOnlyEntity(S src);
}
