package com.kagof.runelite.plugins.brimhavenagility;

import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaLocation;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaNeighbour;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaObstacle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Keeps track of all neighbours of all platforms in the Brimhaven agility arena.
 */
@Slf4j
public final class BrimhavenAgilityArenaNeighbourDigest
{
	private BrimhavenAgilityArenaNeighbourDigest()
	{
	}

	private static volatile boolean loaded = false;
	private static final Map<BrimhavenAgilityArenaLocation, List<BrimhavenAgilityArenaNeighbour>> neighbours = new HashMap<>();
	;

	public static List<BrimhavenAgilityArenaNeighbour> getNeighbours(final BrimhavenAgilityArenaLocation location)
	{
		if (!loaded)
		{
			loadNeighbours();
		}
		return neighbours.getOrDefault(location, List.of());
	}

	/**
	 * Loads the layout of the agility arena from the arena_layout.txt file. This file records all neighbours of
	 * platforms. Each line of the file is 5 characters:
	 * <ol>
	 *     <li>source x coordinate</li>
	 *     <li>source y coordinate</li>
	 *     <li>{@link BrimhavenAgilityArenaObstacle} short code</li>
	 *     <li>destination x coordinate</li>
	 *     <li>destination y coordinate</li>
	 * </ol>
	 * Where the x,y coordinates are in the {@link BrimhavenAgilityArenaLocation} space (ie, 0 to 4).
	 */
	private static synchronized void loadNeighbours()
	{
		if (loaded)
		{
			return;
		}
		log.info("Loading Brimhaven agility arena neighbours from file");
		try (InputStream is = BrimhavenAgilityArenaNeighbourDigest.class.getClassLoader().getResourceAsStream("arena_layout.txt"))
		{
			if (is == null)
			{
				log.error("Neighbours file not found");
			}
			else
			{
				try (InputStreamReader isr = new InputStreamReader(is);
					 BufferedReader br = new BufferedReader(isr))
				{
					br.lines().forEach(line -> {
						int srcx = Integer.parseInt(line.substring(0, 1));
						int srcy = Integer.parseInt(line.substring(1, 2));
						char obs = line.charAt(2);
						int dstx = Integer.parseInt(line.substring(3, 4));
						int dsty = Integer.parseInt(line.substring(4, 5));
						neighbours.merge(BrimhavenAgilityArenaLocation.of(srcx, srcy),
							List.of(BrimhavenAgilityArenaNeighbour.of(dstx, dsty, BrimhavenAgilityArenaObstacle.from(obs))),
							(l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toUnmodifiableList()));
					});
					loaded = true;
				}
			}
		}
		catch (IOException e)
		{
			log.error("failed to load Brimhaven agility arena layout file", e);
		}
	}
}
