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
import net.runelite.api.coords.WorldPoint;

/**
 * Uses the A* path finding algorithm to find the shortest (weighted) path from the player's current location, to the
 * target dispenser. Adapted from <a href="https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode">Wikipedia's
 * pseudocode</a> implementation.
 */
public final class BrimhavenAgilityPathFinder
{
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

		while (!openSet.isEmpty())
		{
			final BrimhavenAgilityArenaLocation current = openSet.poll();
			if (current.equals(end))
			{
				return reconstructPath(cameFrom, current);
			}

			openSet.remove(current);
			for (BrimhavenAgilityArenaNeighbour neighbour : BrimhavenAgilityArenaNeighbourDigest.getNeighbours(current))
			{
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
			return 999999;
		}
		return neighbour.getObstacle().getWeightFunction().apply(config);
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
