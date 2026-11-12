package gent.timdemey.cards.services.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.res.ResourceType;
import gent.timdemey.cards.services.resources.SolShowResourceNameService;
import gent.timdemey.cards.services.resources.SolShowResourceRepository;

class SolShowSoundServiceTest
{
	private static class RecordingSoundService extends SolShowSoundService
	{
		ResourceDescriptor played;

		@Override
		public void play(ResourceDescriptor descriptor)
		{
			played = descriptor;
		}
	}

	@Test
	void selectsSoundByDestinationForEitherPlayersStacks()
	{
		RecordingSoundService sound = new RecordingSoundService();
		for (int player = 0; player < 2; player++)
		{
			sound.playCardPlacement(ReadOnlyEntityFactory.getOrCreateCardStack(
					new CardStack(SolShowCardStackType.LAYDOWN, player)));
			assertEquals(SolShowSoundService.SoundLaydown, sound.played);
		}
		for (String type : new String[] { SolShowCardStackType.MIDDLE, SolShowCardStackType.TURNOVER,
				SolShowCardStackType.SPECIAL, SolShowCardStackType.DEPOT })
		{
			sound.playCardPlacement(ReadOnlyEntityFactory.getOrCreateCardStack(new CardStack(type, 0)));
			assertEquals(ResourceDescriptors.SoundPutDown, sound.played);
		}
	}

	@Test
	void selectsWinningAndLosingSounds()
	{
		RecordingSoundService sound = new RecordingSoundService();
		sound.playGameOutcome(true);
		assertEquals(SolShowSoundService.SoundWin, sound.played);
		sound.playGameOutcome(false);
		assertEquals(SolShowSoundService.SoundLose, sound.played);
	}

	@Test
	void showdownSoundsCanBeResolvedAndDecoded() throws Exception
	{
		SolShowResourceNameService names = new SolShowResourceNameService();
		SolShowResourceRepository repository = new SolShowResourceRepository();
		repository.preload();
		assertEquals("putdown.wav", names.getFilePath(ResourceDescriptors.SoundPutDown));
		for (ResourceDescriptor descriptor : new ResourceDescriptor[] { SolShowSoundService.SoundLaydown,
				SolShowSoundService.SoundWin, SolShowSoundService.SoundLose })
		{
			try (InputStream input = repository.getResourceAsStream(ResourceType.SOUND, names.getFilePath(descriptor)))
			{
				PcmSample sample = SoundMixer.decode(input);
				assertNotNull(sample);
				org.junit.jupiter.api.Assertions.assertTrue(sample.frames > 0, descriptor.toString());
			}
		}
	}
}
