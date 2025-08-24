package com.kagof.runelite.plugins.brimhavenagility;

import com.google.inject.Provides;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaPath;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Brimhaven Agility"
)
public class BrimhavenAgilityPlugin extends Plugin
{
	public static final int AGILITY_ARENA_REGION_ID = 11157;

	@Getter
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BrimhavenAgilityOverlay overlay;

	@Inject
	private BrimhavenAgilityConfig config;

	@Getter
	private int agilityLevel;
	@Getter
	private BrimhavenAgilityArenaPath currentPath;
	@Getter
	private boolean ticketAvailable;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		agilityLevel = client.getBoostedSkillLevel(Skill.AGILITY);
		currentPath = null;
		ticketAvailable = true;
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		BrimhavenAgilityArenaNeighbourDigest.unload();
		agilityLevel = 0;
		currentPath = null;
		ticketAvailable = true;
	}

	@Subscribe
	public void onGameTick(final GameTick tick)
	{
		recomputePathIfNeeded();
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.AGILITYARENA_TICKETAVAILABLE)
		{
			ticketAvailable = event.getValue() > 0;
		}
	}

	private boolean recomputePathIfNeeded()
	{
		boolean changed = false;
		if (isInAgilityArena() && ticketAvailable)
		{
			WorldPoint ticketPosition = client.getHintArrowPoint();
			WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
			if (currentPath != null)
			{
				if (currentPath.hasPathChanged(playerLocation, ticketPosition))
				{
					// see if we can use a sub-path
					currentPath = currentPath.subPath(playerLocation, ticketPosition);
					changed = true;
				}
			}
			if (currentPath == null)
			{
				currentPath = BrimhavenAgilityPathFinder.findPath(playerLocation, ticketPosition, agilityLevel, config);
				changed = true;
			}
		}
		else if (currentPath != null)
		{
			currentPath = null;
			ticketAvailable = true;
			changed = true;
		}
		if (changed)
		{
			log.debug("New Brimhaven Agility Arena path: {}", currentPath);
		}
		return changed;
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals("brimhavenagility"))
		{
			currentPath = null;
			recomputePathIfNeeded();
		}
	}

	@Subscribe
	public void onStatChanged(final StatChanged statChanged)
	{
		if (statChanged.getSkill() == Skill.AGILITY)
		{
			agilityLevel = statChanged.getBoostedLevel();
			currentPath = null;
			recomputePathIfNeeded();
		}

	}

	@Provides
	BrimhavenAgilityConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BrimhavenAgilityConfig.class);
	}


	public boolean isInAgilityArena()
	{
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return false;
		}

		WorldPoint location = local.getWorldLocation();
		if (location == null)
		{
			return false;
		}
		return location.getRegionID() == AGILITY_ARENA_REGION_ID;
	}
}
