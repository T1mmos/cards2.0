package gent.timdemey.cards.services.scaleman;

public final class ImageDefinition {
    public final String path;
    public final String group;
    
    public ImageDefinition(String path, String group)
    {
        this.path = path;
        this.group = group;                
    }
    
  /*  @Override
    public boolean equals(Object obj) {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof ImageDefinition))
        {
            return false;
        }
        ImageDefinition other = ((ImageDefinition) obj);
        
        return path.equals(other.path) && group.equals(other.group);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(path, group);
    }*/
}
