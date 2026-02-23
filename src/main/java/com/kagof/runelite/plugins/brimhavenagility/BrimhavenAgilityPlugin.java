package com.kagof.runelite.plugins.brimhavenagility;

import com.google.inject.Provides;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaPath;
import com.kagof.runelite.plugins.brimhavenagility.overlay.BrimhavenAgilityOverlay;
import com.kagof.runelite.plugins.brimhavenagility.overlay.BrimhavenAgilityPanelOverlay;
import com.kagof.runelite.plugins.brimhavenagility.overlay.BrimhavenAgilityPlankOverlay;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Skill;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
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

	private static final int ENTRY_PAID_VARBIT = VarbitID.AGILITYARENA_CANENTER;
	private static final int COOLDOWN_REQUIRED_VARBIT = VarbitID.AGILITY_ARENA_TELEPORTED_OUT;
	private static final int TICKET_AVAILABLE_VARBIT = VarbitID.AGILITYARENA_TICKETAVAILABLE;
	private static final int KARAMJA_EASY_VARBIT = VarbitID.KARAMJA_EASY_COUNT;
	private static final int KARAMJA_MEDIUM_VARBIT = VarbitID.KARAMJA_MED_COUNT;
	private static final int FOLLOWER_NPC = VarPlayerID.FOLLOWER_NPC;

	private static final int KARAMJA_EASY_TASKS = 10;
	private static final int KARAMJA_MEDIUM_TASKS = 19;

	private static final int KARAMJA_GLOVES_2 = ItemID.ATJUN_GLOVES_MED;
	private static final int KARAMJA_GLOVES_3 = ItemID.ATJUN_GLOVES_HARD;
	private static final int KARAMJA_GLOVES_4 = ItemID.ATJUN_GLOVES_ELITE;

	private static final WorldArea ARENA_ENTRY_AREA = new WorldArea(2805, 3185, 7, 11, 0);

	public static final int AGILITY_ARENA_REGION_ID = 11157;

	@Getter
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BrimhavenAgilityOverlay overlay;

	@Inject
	private BrimhavenAgilityPanelOverlay panelOverlay;

	@Inject
	private BrimhavenAgilityPlankOverlay plankOverlay;

	@Inject
	private BrimhavenAgilityConfig config;

	@Inject
	@Getter
	private BrimhavenAgilityPlankManager plankManager;

	@Getter
	private volatile int agilityLevel;
	@Getter
	private volatile BrimhavenAgilityArenaPath currentPath;
	@Getter
	private volatile boolean ticketAvailable;
	@Getter
	private volatile boolean entryPaid;
	@Getter
	private volatile boolean cooldownPassed;
	private volatile int easyTasksCompleted;
	private volatile int mediumTasksCompleted;
	@Getter
	private volatile boolean hasNoFollower;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		overlayManager.add(panelOverlay);
		overlayManager.add(plankOverlay);
		agilityLevel = client.getBoostedSkillLevel(Skill.AGILITY);
		currentPath = null;
		clientThread.invokeLater(() -> {
			ticketAvailable = client.getVarbitValue(TICKET_AVAILABLE_VARBIT) > 0;
			entryPaid = client.getVarbitValue(ENTRY_PAID_VARBIT) > 0;
			cooldownPassed = client.getVarbitValue(COOLDOWN_REQUIRED_VARBIT) == 0;
			easyTasksCompleted = client.getVarbitValue(KARAMJA_EASY_VARBIT);
			mediumTasksCompleted = client.getVarbitValue(KARAMJA_MEDIUM_VARBIT);
			hasNoFollower = client.getVarpValue(FOLLOWER_NPC) == -1;
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.remove(panelOverlay);
		overlayManager.remove(plankOverlay);
		BrimhavenAgilityArenaNeighbourDigest.unload();
		plankManager.clear();
		agilityLevel = 0;
		currentPath = null;
		ticketAvailable = true;
		easyTasksCompleted = 0;
		mediumTasksCompleted = 0;
		hasNoFollower = true;
	}

	@Subscribe
	public void onGameTick(final GameTick tick)
	{
		recompute();
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event)
	{
		switch (event.getVarbitId())
		{
			case ENTRY_PAID_VARBIT:
				entryPaid = event.getValue() > 0;
				break;
			case COOLDOWN_REQUIRED_VARBIT:
				cooldownPassed = event.getValue() == 0;
				break;
			case TICKET_AVAILABLE_VARBIT:
				ticketAvailable = event.getValue() > 0;
				break;
			case KARAMJA_EASY_VARBIT:
				easyTasksCompleted = event.getValue();
				break;
			case KARAMJA_MEDIUM_VARBIT:
				mediumTasksCompleted = event.getValue();
				break;
		}
		//noinspection SwitchStatementWithTooFewBranches
		switch (event.getVarpId())
		{
			case FOLLOWER_NPC:
				hasNoFollower = event.getValue() == -1;
				break;
		}
	}

	@Subscribe
	public void onGroundObjectSpawned(final GroundObjectSpawned event)
	{
		if (AGILITY_ARENA_REGION_ID == event.getGroundObject().getWorldLocation().getRegionID())
		{
			plankManager.add(event.getGroundObject());
		}
	}

	@Subscribe
	public void onGroundObjectDespawned(final GroundObjectDespawned event)
	{
		if (AGILITY_ARENA_REGION_ID == event.getGroundObject().getWorldLocation().getRegionID())
		{
			plankManager.remove(event.getGroundObject());
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!config.deprioritizeIncorrectPlank())
		{
			return;
		}
		if (MenuAction.of(event.getType()) == MenuAction.GAME_OBJECT_FIRST_OPTION
			&& event.getOption().equals("Walk-on")
			&& event.getTarget().contains("Plank"))
		{
			deprioritizePlankMenuOptionIfNeeded(event);
		}
	}

	private void recompute()
	{
		recomputePlanksIfNeeded();
		recomputePathIfNeeded();
	}

	private void recomputePlanksIfNeeded()
	{
		if (isInAgilityArena() && (config.highlightCorrectPlank() || config.deprioritizeIncorrectPlank()))
		{
			plankManager.recomputeCorrectPlanks();
		}
	}

	private void recomputePathIfNeeded()
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
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals("brimhavenagility"))
		{
			currentPath = null;
			clientThread.invokeLater(this::recompute);
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

	public boolean isNearAgilityArenaEntrance()
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
		return location.isInArea(ARENA_ENTRY_AREA);
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

	public boolean isWearingKaramjaGloves234()
	{
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return false;
		}
		PlayerComposition comp = local.getPlayerComposition();
		if (comp == null)
		{
			return false;
		}
		int gloveSlot = comp.getEquipmentId(KitType.HANDS);
		return gloveSlot == KARAMJA_GLOVES_2 || gloveSlot == KARAMJA_GLOVES_3 || gloveSlot == KARAMJA_GLOVES_4;
	}

	public boolean isMediumDiaryCompleted()
	{
		return easyTasksCompleted == KARAMJA_EASY_TASKS && mediumTasksCompleted == KARAMJA_MEDIUM_TASKS;
	}

	private void deprioritizePlankMenuOptionIfNeeded(final MenuEntryAdded event)
	{
		final WorldView wv = client.getWorldView(event.getMenuEntry().getWorldViewId());
		final WorldPoint point = WorldPoint.fromScene(wv, event.getActionParam0(), event.getActionParam1(), wv.getPlane());
		if (plankManager.isOnBadPlank(point))
		{
			event.getMenuEntry().setDeprioritized(true);
		}
	}
}
