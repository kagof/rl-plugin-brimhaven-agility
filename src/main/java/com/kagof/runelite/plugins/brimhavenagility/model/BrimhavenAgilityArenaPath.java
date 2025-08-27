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
	 * @return the sub-path of this path which starts at the {@code newStartLocation} and ends at the
	 * {@code newEndLocation}. If such a sub-path does not exist, returns {@code null}.
	 */
	public BrimhavenAgilityArenaPath subPath(final WorldPoint newStartLocation, final WorldPoint newEndLocation)
	{
		BrimhavenAgilityArenaLocation sl = BrimhavenAgilityArenaLocation.fromWorldPoint(newStartLocation);
		BrimhavenAgilityArenaLocation el = BrimhavenAgilityArenaLocation.fromWorldPoint(newEndLocation);
		int sIndex = locations.indexOf(sl);
		if (sIndex < 0)
		{
			return null;
		}
		int eIndex = locations.indexOf(el);
		if (eIndex < 0)
		{
			return null;
		}
		if (eIndex < sIndex)
		{
			return null;
		}
		if (sIndex == 0 && eIndex == locations.size() - 1)
		{
			return this;
		}
		return new BrimhavenAgilityArenaPath(List.copyOf(locations.subList(sIndex, eIndex + 1)));
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
