package gent.timdemey.cards.services.sound;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;

public class SolShowSoundService extends SoundService
{
	public static final ResourceDescriptor SoundLaydown = ResourceDescriptors.get("SoundLaydown");
	public static final ResourceDescriptor SoundWin = ResourceDescriptors.get("SoundWin");
	public static final ResourceDescriptor SoundLose = ResourceDescriptors.get("SoundLose");

	@Override
	public void preload()
	{
		super.preload();
		preload(SoundLaydown);
		preload(SoundWin);
		preload(SoundLose);
	}

	@Override
	public void playCardPlacement(ReadOnlyCardStack destination)
	{
		if (SolShowCardStackType.LAYDOWN.equals(destination.getCardStackType()))
		{
			play(SoundLaydown);
		}
		else
		{
			super.playCardPlacement(destination);
		}
	}

	@Override
	public void playGameOutcome(boolean won)
	{
		play(won ? SoundWin : SoundLose);
	}
}
