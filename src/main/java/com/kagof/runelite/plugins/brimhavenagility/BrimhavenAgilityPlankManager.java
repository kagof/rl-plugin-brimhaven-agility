package com.kagof.runelite.plugins.brimhavenagility;

import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaLocation;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityPlankChoice;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GroundObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

@Slf4j
@Singleton
public class BrimhavenAgilityPlankManager
{
	private static final Set<Integer> BAD = Set.of(ObjectID.AGILITYARENA_PLANK_BROKE1);
	private static final Set<Integer> GOOD = Set.of(
		ObjectID.AGILITYARENA_PLANK,
		ObjectID.AGILITYARENA_PLANK2,
		ObjectID.AGILITYARENA_PLANK3,
		ObjectID.AGILITYARENA_PLANK_MIDDLE,
		ObjectID.AGILITYARENA_PLANK2_MIDDLE,
		ObjectID.AGILITYARENA_PLANK3_MIDDLE);

	private static final int PLANE = BrimhavenAgilityArenaLocation.PLANE;

	private static final WorldArea PLANKS1_AREA = new WorldArea(2764, 9556, 6, 3, PLANE);
	private static final WorldArea PLANKS2_AREA = new WorldArea(2797, 9589, 6, 3, PLANE);

	@Getter
	private final GroundObject[][] planks1 = new GroundObject[3][6];
	@Getter
	private final GroundObject[][] planks2 = new GroundObject[3][6];
	private volatile boolean planks1Changed = false;
	private volatile boolean planks2Changed = false;

	@Getter
	private BrimhavenAgilityPlankChoice planks1Choice = BrimhavenAgilityPlankChoice.UNKNOWN;
	@Getter
	private BrimhavenAgilityPlankChoice planks2Choice = BrimhavenAgilityPlankChoice.UNKNOWN;

	@Inject
	public BrimhavenAgilityPlankManager()
	{
	}

	public void clear()
	{
		for (int y = 0; y < planks1.length; y++)
		{
			for (int x = 0; x < planks1[y].length; x++)
			{
				planks1[y][x] = null;
				planks2[y][x] = null;
			}
		}
		planks1Choice = BrimhavenAgilityPlankChoice.UNKNOWN;
		planks1Changed = false;
		planks2Choice = BrimhavenAgilityPlankChoice.UNKNOWN;
		planks2Changed = false;
	}

	public void recomputeCorrectPlanks()
	{
		if (planks1Changed)
		{
			planks1Choice = findCorrectPlank(planks1);
			planks1Changed = false;
			log.debug("planks1Choice recomputed as {}", planks1Choice);
		}
		if (planks2Changed)
		{
			planks2Choice = findCorrectPlank(planks2);
			planks2Changed = false;
			log.debug("planks2Choice recomputed as {}", planks2Choice);
		}
	}

	public boolean add(final GroundObject groundObject)
	{
		return updateVal(groundObject, groundObject);
	}

	public boolean remove(final GroundObject groundObject)
	{
		return updateVal(groundObject, null);
	}

	public boolean isOnBadPlank(final WorldPoint point)
	{
		if (point.isInArea(PLANKS1_AREA))
		{
			if (planks1Choice == BrimhavenAgilityPlankChoice.UNKNOWN)
			{
				return false;
			}
			return point.getY() - PLANKS1_AREA.getY() != planks1Choice.ordinal();
		}
		else if (point.isInArea(PLANKS2_AREA))
		{
			if (planks2Choice == BrimhavenAgilityPlankChoice.UNKNOWN)
			{
				return false;
			}
			return point.getY() - PLANKS2_AREA.getY() != planks2Choice.ordinal();
		}
		else
		{
			return false;
		}
	}

	private BrimhavenAgilityPlankChoice findCorrectPlank(final GroundObject[][] planks)
	{
		for (int y = 0; y < planks.length; y++)
		{
			boolean hasBad = false;
			for (int x = 0; x < planks[y].length; x++)
			{
				if (planks[y][x] == null)
				{
					return BrimhavenAgilityPlankChoice.UNKNOWN;
				}
				if (BAD.contains(planks[y][x].getId()))
				{
					hasBad = true;
					break;
				}
			}
			if (!hasBad)
			{
				if (y > BrimhavenAgilityPlankChoice.values().length)
				{
					log.warn("y value {} greater than number of options; this should not happen", y);
					break;
				}
				return BrimhavenAgilityPlankChoice.values()[y];
			}
		}
		return BrimhavenAgilityPlankChoice.UNKNOWN;
	}

	private boolean updateVal(final GroundObject compareTo, final GroundObject setTo)
	{
		if (compareTo.getPlane() != PLANE)
		{
			return false;
		}
		if (!GOOD.contains(compareTo.getId()) && !BAD.contains(compareTo.getId()))
		{
			return false;
		}
		synchronized (planks1)
		{
			if (compareTo.getWorldLocation().isInArea(PLANKS1_AREA))
			{
				int y = compareTo.getWorldLocation().getY() - PLANKS1_AREA.getY();
				int x = compareTo.getWorldLocation().getX() - PLANKS1_AREA.getX();
				if (y > planks1.length)
				{
					log.warn("y value {} higher than planks1 length; this should not happen", y);
					return false;
				}
				if (x > planks1[y].length)
				{
					log.warn("x value {} higher than planks1[{}] length; this should not happen", x, y);
					return false;
				}
				planks1[y][x] = setTo;
				planks1Changed = true;
				return true;
			}
		}
		synchronized (planks2)
		{
			if (compareTo.getWorldLocation().isInArea(PLANKS2_AREA))
			{
				int y = compareTo.getWorldLocation().getY() - PLANKS2_AREA.getY();
				int x = compareTo.getWorldLocation().getX() - PLANKS2_AREA.getX();
				if (y > planks2.length)
				{
					log.warn("y value {} higher than planks2 length; this should not happen", y);
					return false;
				}
				if (x > planks2[y].length)
				{
					log.warn("x value {} higher than planks2[{}] length; this should not happen", x, y);
					return false;
				}
				planks2[y][x] = setTo;
				planks2Changed = true;
				return true;
			}
		}
		return false;
	}
}
