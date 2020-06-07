package gent.timdemey.cards.services.scaleman;

/**
 * Represents a background task where a scalable resource must be rescaled in some given dimensions.
 * @author Timmos
 *
 */
class ScalableResourceRescaleTask
{
    final IScalableResource scaleResource;
    final int width;
    final int height;
    
    ScalableResourceRescaleTask(IScalableResource scaleResource, int width, int height)
    {
        this.scaleResource = scaleResource;
        this.width = width;
        this.height = height;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + scaleResource.getId().hashCode();
        result = prime * result + width;
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
        ScalableResourceRescaleTask other = (ScalableResourceRescaleTask) obj;
        if (height != other.height)
            return false;
        if (!scaleResource.getId().equals(other.scaleResource.getId()))
            return false;
        if (width != other.width)
            return false;
        return true;
    }
    
    
}
