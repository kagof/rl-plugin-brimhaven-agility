package com.kagof.runelite.plugins.brimhavenagility.model;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPathFinder;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrimhavenAgilityArenaObstacle
{
	// Blade takes 4 to 6 ticks, so averaging it as 5
	BLADE('b', 1, 5, BrimhavenAgilityConfig::bladeAvoid),
	ROPE_SWING('s', 1, 4, BrimhavenAgilityConfig::ropeSwingAvoid),
	LOW_WALL('w', 1, 5, BrimhavenAgilityConfig::lowWallAvoid),
	PLANK('p', 1, 9, BrimhavenAgilityConfig::plankAvoid),
	BALANCING_ROPE('r', 1, 9, BrimhavenAgilityConfig::balancingRopeAvoid),
	LOG_BALANCE('o', 1, 9, BrimhavenAgilityConfig::logBalanceAvoid),
	BALANCING_LEDGE('l', 1, 9, BrimhavenAgilityConfig::balancingLedgeAvoid),
	MONKEY_BARS('m', 1, 13, BrimhavenAgilityConfig::monkeyBarsAvoid),
	PILLAR('i', 1, 9, BrimhavenAgilityConfig::pillarAvoid),
	PRESSURE_PAD('a', 20, 4, BrimhavenAgilityConfig::pressurePadAvoid),
	FLOOR_SPIKES('f', 20, 4, BrimhavenAgilityConfig::floorSpikesAvoid),
	HAND_HOLDS('h', 20, 10, BrimhavenAgilityConfig::handHoldsAvoid),
	SPINNING_BLADES('n', 40, 5, BrimhavenAgilityConfig::spinningBladesAvoid),
	DARTS('d', 40, 10, BrimhavenAgilityConfig::dartsAvoid),
	IMPASSABLE('x', 999999, BrimhavenAgilityPathFinder.NEVER_USE_WEIGHT, i -> true);

	private final char shortForm;
	private final int minLevel;
	private final int weight; // weights are taken from the number of ticks each obstacle takes according to the wiki
	private final Function<BrimhavenAgilityConfig, Boolean> avoidFunction;

	private static final Map<Character, BrimhavenAgilityArenaObstacle> shortToObs = Arrays
		.stream(BrimhavenAgilityArenaObstacle.values())
		.collect(Collectors.toMap(BrimhavenAgilityArenaObstacle::getShortForm, Function.identity()));

	public static BrimhavenAgilityArenaObstacle from(char shortForm)
	{
		return shortToObs.getOrDefault(shortForm, IMPASSABLE);
	}
}
