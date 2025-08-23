package com.kagof.runelite.plugins.brimhavenagility.model;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityArenaNeighbourDigest;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPlugin;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

/**
 * A location of a platform in the Agility Arena. Each platform is given an x & y coordinate from 0 to 4, starting from
 * the southwest corner. These points may be mapped to or from a {@link WorldPoint}.
 */
@Value
@EqualsAndHashCode
public class BrimhavenAgilityArenaLocation
{
	private static final int PLATFORM_CENTER_OFFSET = 5;
	private static final int PLATFORM_WIDTH = 11;
	private static final int PLATFORM_HEIGHT = 11;
	private static final int PLATFORM_START_X = 9;
	private static final int PLATFORM_START_Y = 10;
	private static final int PLANE = 3;

	int x;
	int y;

	public List<BrimhavenAgilityArenaNeighbour> getNeighbours()
	{
		return BrimhavenAgilityArenaNeighbourDigest.getNeighbours(this);
	}

	/**
	 * @return the WorldPoint of the center of the platform.
	 */
	public WorldPoint toCenteredWorldPoint()
	{
		return WorldPoint.fromRegion(BrimhavenAgilityPlugin.AGILITY_ARENA_REGION_ID,
			PLATFORM_START_X + (x * PLATFORM_WIDTH),
			PLATFORM_START_Y + (y * PLATFORM_HEIGHT),
			3);
	}

	public static BrimhavenAgilityArenaLocation fromWorldPoint(WorldPoint worldPoint)
	{
		if (worldPoint == null)
		{
			return null;
		}
		if (worldPoint.getRegionID() != BrimhavenAgilityPlugin.AGILITY_ARENA_REGION_ID)
		{
			return null;
		}
		return new BrimhavenAgilityArenaLocation(
			(worldPoint.getRegionX() + PLATFORM_CENTER_OFFSET - PLATFORM_START_X) / PLATFORM_WIDTH,
			(worldPoint.getRegionY() + PLATFORM_CENTER_OFFSET - PLATFORM_START_Y) / PLATFORM_HEIGHT);
	}

	public static BrimhavenAgilityArenaLocation of(int x, int y)
	{
		return new BrimhavenAgilityArenaLocation(x, y);
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
