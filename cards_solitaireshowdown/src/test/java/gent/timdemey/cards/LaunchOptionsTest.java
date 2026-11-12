package gent.timdemey.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LaunchOptionsTest
{
	@Test
	void acceptsOptionalDeveloperModeAndRejectsInvalidArguments()
	{
		assertFalse(LaunchOptions.parse(new String[] { "plugin" }).devMode);
		LaunchOptions options = LaunchOptions.parse(new String[] { "--devmode", "plugin" });
		assertTrue(options.devMode);
		assertEquals("plugin", options.pluginClassName);
		assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse(new String[] { "--devmode" }));
		assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse(new String[] { "plugin", "--unknown" }));
		assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse(new String[] { "plugin", "--devmode", "--devmode" }));
	}
}
