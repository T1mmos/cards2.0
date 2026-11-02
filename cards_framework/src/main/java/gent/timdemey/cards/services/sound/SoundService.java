package gent.timdemey.cards.services.sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.res.AudioResource;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.services.interfaces.ISoundService;

public class SoundService implements ISoundService, IPreload
{
	private final SoundMixer mixer = new SoundMixer();
	private final Map<ResourceDescriptor, PcmSample> samples = new ConcurrentHashMap<>();
	private volatile boolean muted = false;

	@Override
	public void setMuted(boolean muted)
	{
		this.muted = muted;
	}

	@Override
	public boolean isMuted()
	{
		return muted;
	}

	@Override
	public void preload()
	{
		// load necessary files, decoded up front so play() never touches the disk or a decoder
		preload(ResourceDescriptors.SoundTest);
		preload(ResourceDescriptors.SoundPutDown);

		// the output line stays open and running, so a sound never waits for the line to start
		mixer.start();
	}

	private PcmSample preload(ResourceDescriptor desc)
	{
		IResourceCacheService resCacheServ = Services.get(IResourceCacheService.class);
		IResourceNameService resNameServ = Services.get(IResourceNameService.class);

		String path = resNameServ.getFilePath(desc);
		AudioResource audioRes;
		synchronized (resCacheServ)
		{
			audioRes = resCacheServ.getAudio(path);
		}
		if (audioRes == null || audioRes.raw == null)
		{
			Logger.error("Cannot load audio for descriptor " + desc);
			return null;
		}
		samples.put(desc, audioRes.raw);
		return audioRes.raw;
	}

	@Override
	public void play(ResourceDescriptor desc)
	{
		if (muted)
		{
			return;
		}
		PcmSample sample = samples.get(desc);
		if (sample == null)
		{
			Logger.warn("Sound %s was not preloaded, loading it now", desc);
			sample = preload(desc);
		}
		mixer.play(sample);
	}
}
