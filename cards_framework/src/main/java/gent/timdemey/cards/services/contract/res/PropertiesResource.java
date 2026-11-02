package gent.timdemey.cards.services.contract.res;

import java.util.Properties;

public class PropertiesResource extends Resource<Properties>
{
	public PropertiesResource(String filename, boolean fallback, Properties props)
	{
		super(filename, fallback, props);
	}
}
