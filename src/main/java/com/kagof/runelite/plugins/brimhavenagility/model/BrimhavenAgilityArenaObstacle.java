package com.kagof.runelite.plugins.brimhavenagility.model;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BrimhavenAgilityArenaObstacle
{
	BLADE('b', 1, BrimhavenAgilityConfig::bladeWeight),
	ROPE_SWING('s', 1, BrimhavenAgilityConfig::ropeSwingWeight),
	LOW_WALL('w', 1, BrimhavenAgilityConfig::lowWallWeight),
	PLANK('p', 1, BrimhavenAgilityConfig::plankWeight),
	BALANCING_ROPE('r', 1, BrimhavenAgilityConfig::balancingRopeWeight),
	LOG_BALANCE('o', 1, BrimhavenAgilityConfig::logBalanceWeight),
	BALANCING_LEDGE('l', 1, BrimhavenAgilityConfig::balancingLedgeWeight),
	MONKEY_BARS('m', 1, BrimhavenAgilityConfig::monkeyBarsWeight),
	PILLAR('i', 1, BrimhavenAgilityConfig::pillarWeight),
	PRESSURE_PAD('a', 20, BrimhavenAgilityConfig::pressurePadWeight),
	FLOOR_SPIKES('f', 20, BrimhavenAgilityConfig::floorSpikesWeight),
	HAND_HOLDS('h', 20, BrimhavenAgilityConfig::handHoldsWeight),
	SPINNING_BLADES('n', 40, BrimhavenAgilityConfig::spinningBladesWeight),
	DARTS('d', 40, BrimhavenAgilityConfig::dartsWeight),
	IMPASSABLE('x', 999999, i -> 999999);

	@Getter
	private final char shortForm;
	@Getter
	private final int minLevel;
	@Getter
	private Function<BrimhavenAgilityConfig, Integer> weightFunction;

	private static Map<Character, BrimhavenAgilityArenaObstacle> shortToObs = Arrays.stream(BrimhavenAgilityArenaObstacle.values())
		.collect(Collectors.toMap(BrimhavenAgilityArenaObstacle::getShortForm, Function.identity()));

	public static BrimhavenAgilityArenaObstacle from(char shortForm)
	{
		return shortToObs.getOrDefault(shortForm, IMPASSABLE);
	}
}
