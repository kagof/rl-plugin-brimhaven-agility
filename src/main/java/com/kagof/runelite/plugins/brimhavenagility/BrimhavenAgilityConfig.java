package com.kagof.runelite.plugins.brimhavenagility;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("brimhavenagility")
public interface BrimhavenAgilityConfig extends Config
{

	@ConfigItem(keyName = "drawpath",
		name = "Draw path",
		description = "Whether or not to draw the shortest path to the currently active dispenser",
		position = 0)
	default boolean drawPath()
	{
		return true;
	}

	@Alpha
	@ConfigItem(keyName = "pathcolour",
		name = "Path colour",
		description = "The colour used to draw the path to the currently active dispenser",
		position = 1)
	default Color pathColour()
	{
		return new Color(255, 255, 255, 191);
	}

	@ConfigSection(
		name = "Path Weights",
		description = "Configuration for the weights of obstacles in the Brimhaven Agility Arena",
		position = 2)
	String pathWeights = "pathweights";

	// the default weights are taken from the number of ticks each obstacle takes according to the wiki

	@ConfigItem(keyName = "bladeweight",
		name = "Blade weight",
		description = "Weighting of the blade obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int bladeWeight()
	{
		return 5; // 4 to 6 ticks, so averaging it as 5
	}

	@ConfigItem(keyName = "ropeswingweight",
		name = "Rope swing weight",
		description = "Weighting of the rope swing obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int ropeSwingWeight()
	{
		return 4;
	}

	@ConfigItem(keyName = "lowwallweight",
		name = "Low wall weight",
		description = "Weighting of the low wall obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int lowWallWeight()
	{
		return 5;
	}

	@ConfigItem(keyName = "plankweight",
		name = "Plank weight",
		description = "Weighting of the plank obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int plankWeight()
	{
		return 9;
	}

	@ConfigItem(keyName = "balancingropeweight",
		name = "Balancing rope weight",
		description = "Weighting of the balancing rope obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int balancingRopeWeight()
	{
		return 9;
	}

	@ConfigItem(keyName = "logbalanceweight",
		name = "Log balance weight",
		description = "Weighting of the log balance obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int logBalanceWeight()
	{
		return 9;
	}

	@ConfigItem(keyName = "balancingledgeweight",
		name = "Balancing ledge weight",
		description = "Weighting of the balancing ledge obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int balancingLedgeWeight()
	{
		return 9;
	}

	@ConfigItem(keyName = "monkeybarsweight",
		name = "Monkey bars weight",
		description = "Weighting of the monkey bars obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int monkeyBarsWeight()
	{
		return 13;
	}

	@ConfigItem(keyName = "pillarweight",
		name = "Pillar weight",
		description = "Weighting of the pillar obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int pillarWeight()
	{
		return 9;
	}

	@ConfigItem(keyName = "pressurepadweight",
		name = "Pressure pad weight",
		description = "Weighting of the pressure pad obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int pressurePadWeight()
	{
		return 4;
	}

	@ConfigItem(keyName = "floorspikesweight",
		name = "Floor spikes weight",
		description = "Weighting of the floor spikes obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int floorSpikesWeight()
	{
		return 4;
	}

	@ConfigItem(keyName = "handholdsweight",
		name = "Hand holds weight",
		description = "Weighting of the hand holds obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int handHoldsWeight()
	{
		return 10;
	}

	@ConfigItem(keyName = "spinningbladesweight",
		name = "Spinning blades weight",
		description = "Weighting of the spinning blades obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int spinningBladesWeight()
	{
		return 5;
	}

	@ConfigItem(keyName = "dartsweight",
		name = "Darts weight",
		description = "Weighting of the darts obstacle",
		section = pathWeights)
	@Range(min = 1, max = 999999)
	default int dartsWeight()
	{
		return 10;
	}
}
