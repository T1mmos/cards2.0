package gent.timdemey.cards.services.contract;

import java.awt.Dimension;

import gent.timdemey.cards.ui.components.ISResource;

public class RescaleRequest
{
    public final ISResource<?> scalableResource;
    public final Dimension dimension;

    public RescaleRequest(ISResource<?> scalableResource, Dimension dimension)
    {
        this.scalableResource = scalableResource;
        this.dimension = dimension;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimension.hashCode();
        result = prime * result + scalableResource.getId().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RescaleRequest other = (RescaleRequest) obj;
        if (!dimension.equals(other.dimension))
            return false;
        if (!scalableResource.getId().equals(other.scalableResource.getId()))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "RescaleRequest: file=" + scalableResource.getResource().filename + ", dim=" + dimension; 
    }
}
