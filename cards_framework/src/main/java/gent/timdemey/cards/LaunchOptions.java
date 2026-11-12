package gent.timdemey.cards;

public final class LaunchOptions
{
	public final String pluginClassName;
	public final boolean devMode;

	private LaunchOptions(String pluginClassName, boolean devMode)
	{
		this.pluginClassName = pluginClassName;
		this.devMode = devMode;
	}

	public static LaunchOptions parse(String[] args)
	{
		String plugin = null;
		boolean devMode = false;
		for (String arg : args)
		{
			if ("--devmode".equals(arg) && !devMode)
			{
				devMode = true;
			}
			else if (!arg.startsWith("--") && plugin == null && !arg.isEmpty())
			{
				plugin = arg;
			}
			else
			{
				throw new IllegalArgumentException("Unexpected argument: " + arg);
			}
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("A plugin class name is required.");
		}
		return new LaunchOptions(plugin, devMode);
	}
}
