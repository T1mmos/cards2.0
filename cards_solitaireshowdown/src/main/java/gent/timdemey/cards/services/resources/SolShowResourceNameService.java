package gent.timdemey.cards.services.resources;

import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.sound.SolShowSoundService;

public class SolShowResourceNameService extends ResourceNameService
{
	@Override
	public String getFilePath(ResourceDescriptor descriptor)
	{
		if (SolShowSoundService.SoundLaydown.equals(descriptor))
		{
			return "laydown.wav";
		}
		if (SolShowSoundService.SoundWin.equals(descriptor))
		{
			return "trumpet-sound-effect.wav";
		}
		if (SolShowSoundService.SoundLose.equals(descriptor))
		{
			return "epic-fail-trumpet.wav";
		}
		return super.getFilePath(descriptor);
	}
}
