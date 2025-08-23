package com.kagof.runelite.plugins.brimhavenagility.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

/**
 * A path between two points in the agility arena.
 */
@Value
public class BrimhavenAgilityArenaPath
{
	List<BrimhavenAgilityArenaLocation> locations;

	public int size()
	{
		return locations.size();
	}

	public boolean hasPathChanged(final WorldPoint playerLocation, final WorldPoint dispenserLocation)
	{
		if (locations.isEmpty())
		{
			return true;
		}
		return !locations.get(0).equals(BrimhavenAgilityArenaLocation.fromWorldPoint(playerLocation))
			|| !locations.get(locations.size() - 1).equals(BrimhavenAgilityArenaLocation.fromWorldPoint(dispenserLocation));
	}

	/**
	 * @return the sub-path of this path which starts at the {@code newPlayerLocation} and ends at the
	 * {@code newDispenserLocation}. If such a sub-path does not exist, returns {@code null}.
	 */
	public BrimhavenAgilityArenaPath subPath(final WorldPoint newPlayerLocation, final WorldPoint newDispenserLocation)
	{
		BrimhavenAgilityArenaLocation pl = BrimhavenAgilityArenaLocation.fromWorldPoint(newPlayerLocation);
		BrimhavenAgilityArenaLocation dl = BrimhavenAgilityArenaLocation.fromWorldPoint(newDispenserLocation);
		int pIndex = locations.indexOf(pl);
		if (pIndex == -1)
		{
			return null;
		}
		int dIndex = locations.indexOf(dl);
		if (dIndex == -1)
		{
			return null;
		}
		if (dIndex < pIndex)
		{
			return null;
		}
		if (pIndex == 0 && dIndex == locations.size() - 1)
		{
			return this;
		}
		return new BrimhavenAgilityArenaPath(locations.subList(pIndex, pIndex + 1));
	}

	public List<WorldPoint> toWorldPoints()
	{
		return locations.stream()
			.map(BrimhavenAgilityArenaLocation::toCenteredWorldPoint)
			.collect(Collectors.toList());
	}

	@Override
	public String toString()
	{
		return locations.stream()
			.map(BrimhavenAgilityArenaLocation::toString)
			.collect(Collectors.joining("->"));
	}
}
