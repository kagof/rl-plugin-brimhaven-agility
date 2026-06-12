package com.kagof.runelite.plugins.brimhavenagility.model;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class BrimhavenAgilityArenaNeighbour implements Comparable<BrimhavenAgilityArenaNeighbour>
{
	BrimhavenAgilityArenaLocation location;
	BrimhavenAgilityArenaObstacle obstacle;

	public static BrimhavenAgilityArenaNeighbour of(int x, int y, BrimhavenAgilityArenaObstacle obstacle)
	{
		return new BrimhavenAgilityArenaNeighbour(BrimhavenAgilityArenaLocation.of(x, y), obstacle);
	}

	@Override
	public int compareTo(BrimhavenAgilityArenaNeighbour o)
	{
		return this.getLocation().compareTo(o.getLocation());
	}
}
