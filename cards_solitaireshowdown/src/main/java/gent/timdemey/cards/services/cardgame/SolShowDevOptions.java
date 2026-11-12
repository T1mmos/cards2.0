package gent.timdemey.cards.services.cardgame;

public final class SolShowDevOptions
{
	private final boolean enabled;
	private volatile boolean easyDistribution;

	public SolShowDevOptions(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public boolean isEasyDistribution()
	{
		return enabled && easyDistribution;
	}

	public void setEasyDistribution(boolean selected)
	{
		if (selected && !enabled)
		{
			throw new IllegalStateException("Developer mode is not enabled");
		}
		easyDistribution = selected;
	}
}
