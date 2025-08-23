package com.kagof.runelite.plugins.brimhavenagility.questhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
import static net.runelite.api.Constants.CHUNK_SIZE;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;


/**
 * The contents of this file were taken from the <a href="https://github.com/Zoinkwiz/quest-helper">Quest Helper plugin</a>.
 */
public class QuestPerspective
{
	public static Collection<WorldPoint> toLocalInstance(Client client, WorldPoint worldPoint)
	{
		if (!client.isInInstancedRegion())
		{
			return Collections.singleton(worldPoint);
		}

		// find instance chunks using the template point. there might be more than one.
		List<WorldPoint> worldPoints = new ArrayList<>();

		int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
		for (int z = 0; z < instanceTemplateChunks.length; ++z)
		{
			for (int x = 0; x < instanceTemplateChunks[z].length; ++x)
			{
				for (int y = 0; y < instanceTemplateChunks[z][x].length; ++y)
				{
					int chunkData = instanceTemplateChunks[z][x][y];
					int rotation = chunkData >> 1 & 0x3;
					int templateChunkY = (chunkData >> 3 & 0x7FF) * CHUNK_SIZE;
					int templateChunkX = (chunkData >> 14 & 0x3FF) * CHUNK_SIZE;
					if (worldPoint.getX() >= templateChunkX && worldPoint.getX() < templateChunkX + CHUNK_SIZE
						&& worldPoint.getY() >= templateChunkY && worldPoint.getY() < templateChunkY + CHUNK_SIZE)
					{
						WorldPoint p =
							new WorldPoint(client.getBaseX() + x * CHUNK_SIZE + (worldPoint.getX() & (CHUNK_SIZE - 1)),
								client.getBaseY() + y * CHUNK_SIZE + (worldPoint.getY() & (CHUNK_SIZE - 1)),
								z);
						p = rotate(p, rotation);
						if (p.isInScene(client))
						{
							worldPoints.add(p);
						}
					}
				}
			}
		}
		return worldPoints;
	}

	private static WorldPoint rotate(WorldPoint point, int rotation)
	{
		int chunkX = point.getX() & ~(CHUNK_SIZE - 1);
		int chunkY = point.getY() & ~(CHUNK_SIZE - 1);
		int x = point.getX() & (CHUNK_SIZE - 1);
		int y = point.getY() & (CHUNK_SIZE - 1);
		switch (rotation)
		{
			case 1:
				return new WorldPoint(chunkX + y, chunkY + (CHUNK_SIZE - 1 - x), point.getPlane());
			case 2:
				return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - x), chunkY + (CHUNK_SIZE - 1 - y), point.getPlane());
			case 3:
				return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - y), chunkY + x, point.getPlane());
		}
		return point;
	}

	public static LocalPoint getInstanceLocalPoint(Client client, WorldPoint wp)
	{
		WorldPoint instanceWorldPoint = getInstanceWorldPoint(client, wp);
		if (instanceWorldPoint == null)
		{
			return null;
		}

		return LocalPoint.fromWorld(client, instanceWorldPoint);
	}

	public static WorldPoint getInstanceWorldPoint(Client client, WorldPoint wp)
	{
		Collection<WorldPoint> points = QuestPerspective.toLocalInstance(client, wp);

		for (WorldPoint point : points)
		{
			if (point != null)
			{
				return point;
			}
		}
		return null;
	}
}
