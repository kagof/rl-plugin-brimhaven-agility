package com.kagof.runelite.plugins.brimhavenagility;

import com.google.common.collect.Lists;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaLocation;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaNeighbour;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaPath;
import com.kagof.runelite.plugins.brimhavenagility.model.MapWithDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

/**
 * Uses the A* path finding algorithm to find the shortest (weighted) path from the player's current location, to the
 * target dispenser. Adapted from <a href="https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode">Wikipedia's
 * pseudocode</a> implementation.
 */
@Slf4j
public final class BrimhavenAgilityPathFinder
{
	//belts & braces to ensure we never have an infinite loop

	private static final int MAX_VISITED_NODES = 25;

	// There are 25 nodes in the arena, each with at most 4 neighbours; we should never exceed this number
	private static final int MAX_TOTAL_NEIGHBOURS = 100;

	// not MAX_INT to avoid overflows
	public static final int NEVER_USE_WEIGHT = 999999;

	// lower than the NEVER_USER_WEIGHT so a valid path can still be found if too many obstacles are avoided
	public static final int AVOID_WEIGHT = 999;

	private BrimhavenAgilityPathFinder()
	{
	}

	public static BrimhavenAgilityArenaPath findPath(final WorldPoint playerLocation,
													 final WorldPoint dispenserLocation,
													 final int playerAgilityLevel,
													 final BrimhavenAgilityConfig config)
	{
		final BrimhavenAgilityArenaLocation start = BrimhavenAgilityArenaLocation.fromWorldPoint(playerLocation);
		final BrimhavenAgilityArenaLocation end = BrimhavenAgilityArenaLocation.fromWorldPoint(dispenserLocation);

		if (start == null || end == null)
		{
			return null;
		}

		Map<BrimhavenAgilityArenaLocation, Integer> gScore = new MapWithDefault<>(999999);
		gScore.put(start, 0);

		Map<BrimhavenAgilityArenaLocation, Integer> fScore = new MapWithDefault<>(999999);
		fScore.put(start, 0);

		PriorityQueue<BrimhavenAgilityArenaLocation> openSet = new PriorityQueue<>(Comparator.comparing(fScore::get));
		openSet.add(start);

		Map<BrimhavenAgilityArenaLocation, BrimhavenAgilityArenaLocation> cameFrom = new HashMap<>();

		int numVisitedNodes = 0;
		int totalNeighboursExamined = 0;
		while (!openSet.isEmpty())
		{
			final BrimhavenAgilityArenaLocation current = openSet.poll();
			numVisitedNodes++;
			if (current.equals(end))
			{
				return reconstructPath(cameFrom, current);
			}
			if (numVisitedNodes > MAX_VISITED_NODES)
			{
				log.warn("exceeded maximum number of nodes {}. This indicates a potential infinite loop", MAX_VISITED_NODES);
				return null;
			}

			openSet.remove(current);
			for (BrimhavenAgilityArenaNeighbour neighbour : BrimhavenAgilityArenaNeighbourDigest.getNeighbours(current))
			{
				totalNeighboursExamined++;
				if (totalNeighboursExamined > MAX_TOTAL_NEIGHBOURS)
				{
					log.warn("exceeded maximum number of neighbours examined {}. This indicates a potential infinite loop", MAX_TOTAL_NEIGHBOURS);
					return null;
				}
				if (neighbour.getLocation().equals(current))
				{
					log.warn("node {} marked as neighbour of itself", current);
					continue;
				}
				int tentativeGScore = gScore.get(current) + weightedDistance(neighbour, playerAgilityLevel, config);
				if (tentativeGScore < gScore.get(neighbour.getLocation()))
				{
					cameFrom.put(neighbour.getLocation(), current);
					gScore.put(neighbour.getLocation(), tentativeGScore);
					fScore.put(neighbour.getLocation(), tentativeGScore + heuristic(start, neighbour.getLocation()));
					if (!openSet.contains(neighbour.getLocation()))
					{
						openSet.add(neighbour.getLocation());
					}
				}
			}
		}
		return null;
	}

	private static int weightedDistance(BrimhavenAgilityArenaNeighbour neighbour, int agilityLevel, BrimhavenAgilityConfig config)
	{
		if (neighbour.getObstacle().getMinLevel() > agilityLevel)
		{
			return NEVER_USE_WEIGHT;
		}
		if (neighbour.getObstacle().getAvoidFunction().apply(config))
		{
			return AVOID_WEIGHT;
		}

		return neighbour.getObstacle().getWeight();
	}

	private static BrimhavenAgilityArenaPath reconstructPath(final Map<BrimhavenAgilityArenaLocation, BrimhavenAgilityArenaLocation> cameFrom, final BrimhavenAgilityArenaLocation end)
	{
		List<BrimhavenAgilityArenaLocation> path = new ArrayList<>();
		path.add(end);
		BrimhavenAgilityArenaLocation current = end;
		while (cameFrom.containsKey(current))
		{
			current = cameFrom.get(current);
			path.add(current);
		}
		return new BrimhavenAgilityArenaPath(List.copyOf(Lists.reverse(path)));
	}

	/**
	 * The heuristic is the taxicab distance as the player can only move between platforms in this way.
	 */
	private static int heuristic(final BrimhavenAgilityArenaLocation start, final BrimhavenAgilityArenaLocation end)
	{
		return Math.abs(end.getX() - start.getX()) + Math.abs(end.getY() - start.getY());
	}
}
