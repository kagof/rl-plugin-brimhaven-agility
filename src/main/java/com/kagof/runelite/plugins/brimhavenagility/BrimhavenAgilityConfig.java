package com.kagof.runelite.plugins.brimhavenagility;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("brimhavenagility")
public interface BrimhavenAgilityConfig extends Config
{

	@ConfigItem(keyName = "showentrypanel",
		name = "Show entry panel",
		description = "Whether to show the textbox indicating info about entering the arena, when near the entrance",
		position = 0)
	default boolean showEntryPanel()
	{
		return true;
	}

	@ConfigItem(keyName = "showglovewarning",
		name = "Show glove warning",
		description = "Whether to show the warning when not wearing the Karamja medium+ diary gloves when near or in the arena",
		position = 1)
	default boolean showGloveWarning()
	{
		return true;
	}

	@ConfigItem(keyName = "drawpath",
		name = "Draw path",
		description = "Whether or not to draw the shortest path to the currently active dispenser",
		position = 2)
	default boolean drawPath()
	{
		return true;
	}

	@Alpha
	@ConfigItem(keyName = "pathcolour",
		name = "Path colour",
		description = "The colour used to draw the path to the currently active dispenser",
		position = 3)
	default Color pathColour()
	{
		return new Color(255, 255, 255, 191);
	}

	@ConfigItem(keyName = "notifyTicketAvailable",
		name = "Notify ticket available",
		description = "Send a notification when a ticket is available",
		position = 4)
	default Notification notifyTicketAvailable()
	{
		return Notification.OFF;
	}

	@ConfigItem(keyName = "ticketClaimedSound",
		name = "Ticket claimed sound",
		description = "Play a sound when a ticket is claimed",
		position = 5)
	default boolean ticketClaimedSound()
	{
		return false;
	}

	@ConfigItem(keyName = "ticketClaimedSoundId",
		name = "Sound ID",
		description = "The ID of the sound effect to play when a ticket is claimed",
		position = 6)
	default int ticketClaimedSoundId()
	{
		return 2655;
	}

	@ConfigItem(keyName = "ticketAvailableSound",
		name = "Ticket available sound",
		description = "Play a sound when a ticket is available",
		position = 7)
	default boolean ticketAvailableSound()
	{
		return false;
	}

	@ConfigItem(keyName = "ticketAvailableSoundId",
		name = "Sound ID",
		description = "The ID of the sound effect to play when a ticket is available",
		position = 8)
	default int ticketAvailableSoundId()
	{
		return 9512;
	}

	@Range(max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "soundVolume",
		name = "Sound volume",
		description = "Volume of ticket sound effects",
		position = 9)
	default int soundVolume()
	{
		return 50;
	}

	@ConfigItem(keyName = "hideHintArrow",
		name = "Hide hint arrow",
		description = "Hide the hint arrow when ticket is claimed",
		position = 10)
	default boolean hideHintArrow()
	{
		return false;
	}

	@ConfigSection(closedByDefault = true,
		name = "Obstacles to avoid",
		description = "Configuration of obstacles to avoid when computing the path to the active dispenser",
		position = 11)
	String obstaclesAvoid = "obstaclesavoid";

	@ConfigItem(keyName = "bladeavoid",
		name = "Avoid Blade",
		description = "If checked, the recommended path will not contain the blade obstacle",
		section = obstaclesAvoid)
	default boolean bladeAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "ropeswingavoid",
		name = "Avoid Rope swing",
		description = "If checked, the recommended path will not contain the rope swing obstacle",
		section = obstaclesAvoid)
	default boolean ropeSwingAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "lowwallavoid",
		name = "Avoid Low wall",
		description = "If checked, the recommended path will not contain the low wall obstacle",
		section = obstaclesAvoid)
	default boolean lowWallAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "plankavoid",
		name = "Avoid Plank",
		description = "If checked, the recommended path will not contain the plank obstacle",
		section = obstaclesAvoid)
	default boolean plankAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "balancingropeavoid",
		name = "Avoid Balancing rope",
		description = "If checked, the recommended path will not contain the balancing rope obstacle",
		section = obstaclesAvoid)
	default boolean balancingRopeAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "logbalanceavoid",
		name = "Avoid Log balance",
		description = "If checked, the recommended path will not contain the log balance obstacle",
		section = obstaclesAvoid)
	default boolean logBalanceAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "balancingledgeavoid",
		name = "Avoid Balancing ledge",
		description = "If checked, the recommended path will not contain the balancing ledge obstacle",
		section = obstaclesAvoid)
	default boolean balancingLedgeAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "monkeybarsavoid",
		name = "Avoid Monkey bars",
		description = "If checked, the recommended path will not contain the monkey bars obstacle",
		section = obstaclesAvoid)
	default boolean monkeyBarsAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "pillaravoid",
		name = "Avoid Pillar",
		description = "If checked, the recommended path will not contain the pillar obstacle",
		section = obstaclesAvoid)
	default boolean pillarAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "pressurepadavoid",
		name = "Avoid Pressure pad",
		description = "If checked, the recommended path will not contain the pressure pad obstacle",
		section = obstaclesAvoid)
	default boolean pressurePadAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "floorspikesavoid",
		name = "Avoid Floor spikes",
		description = "If checked, the recommended path will not contain the floor spikes obstacle",
		section = obstaclesAvoid)
	default boolean floorSpikesAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "handholdsavoid",
		name = "Avoid Hand holds",
		description = "If checked, the recommended path will not contain the hand holds obstacle",
		section = obstaclesAvoid)
	default boolean handHoldsAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "spinningbladesavoid",
		name = "Avoid Spinning blades",
		description = "If checked, the recommended path will not contain the spinning blades obstacle",
		section = obstaclesAvoid)
	default boolean spinningBladesAvoid()
	{
		return false;
	}

	@ConfigItem(keyName = "dartsavoid",
		name = "Avoid Darts",
		description = "If checked, the recommended path will not contain the darts obstacle",
		section = obstaclesAvoid)
	default boolean dartsAvoid()
	{
		return false;
	}
}
