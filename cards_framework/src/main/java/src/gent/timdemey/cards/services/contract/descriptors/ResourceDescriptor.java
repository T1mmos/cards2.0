package gent.timdemey.cards.services.contract.descriptors;

import java.util.function.Function;

public class ResourceDescriptor extends DescriptorBase
{
    protected ResourceDescriptor(String id)
    {
        super(id);
    }
    
    public static class ResourceDescriptorP1<P1> extends DescriptorBase
    {
        protected final Function<P1, String> param1ToString;
        
        protected ResourceDescriptorP1(String id, Function<P1, String> param1ToString)
        {
            super(id);
            this.param1ToString = param1ToString;
        }
        
        public String get(String filename, P1 param1)
        {
            String param1_str = param1ToString.apply(param1);
            return String.format(id, param1_str);
        }
    }
    
    public static class ResourceDescriptorP2<P1, P2> extends DescriptorBase
    {
        protected final Function<P1, String> param1ToString;
        protected final Function<P2, String> param2ToString;
        
        protected ResourceDescriptorP2(String id, Function<P1, String> param1ToString, Function<P2, String> param2ToString)
        {
            super(id);
            this.param1ToString = param1ToString;
            this.param2ToString = param2ToString;
        }
        
        public String get(String filename, P1 param1, P2 param2)
        {
            String param1_str = param1ToString.apply(param1);
            String param2_str = param2ToString.apply(param2);
            
            return String.format(filename, param1_str, param2_str);
        }
    }
}
