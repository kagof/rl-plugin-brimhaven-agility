package com.kagof.runelite.plugins.brimhavenagility;

import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaLocation;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaPath;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityPlankChoice;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

@Slf4j
@Singleton
public class BrimhavenAgilityWorldPathConverter
{
	private static final Set<Integer> PLANKS_1_X = Set.of(0, 1);
	private static final int PLANKS_1_Y = 1;
	private static final Set<Integer> PLANKS_2_X = Set.of(3, 4);
	private static final int PLANKS_2_Y = 4;

	private final BrimhavenAgilityPlankManager plankManager;
	private final BrimhavenAgilityConfig config;

	private int prevComputedHash = -1;
	private List<WorldPoint> prevComputedWorldPoints = null;

	@Inject
	public BrimhavenAgilityWorldPathConverter(final BrimhavenAgilityPlankManager plankManager, final BrimhavenAgilityConfig config)
	{
		this.plankManager = plankManager;
		this.config = config;
	}

	public synchronized List<WorldPoint> toWorldPoints(final BrimhavenAgilityArenaPath path)
	{
		// prevents us from unnecessarily performing this conversion over and over
		final int hash = Objects.hash(path,
			plankManager.getPlanks1Choice(),
			plankManager.getPlanks2Choice(),
			config.adjustPathForPlanks());
		if (hash == prevComputedHash && prevComputedWorldPoints != null)
		{
			return prevComputedWorldPoints;
		}

		log.debug("path, planks or config has changed; recomputing world points");
		prevComputedHash = hash;
		if (!config.adjustPathForPlanks() || path.getLocations().size() < 2)
		{
			prevComputedWorldPoints = path.getLocations().stream()
				.map(BrimhavenAgilityArenaLocation::toCenteredWorldPoint)
				.collect(Collectors.toList());
			return prevComputedWorldPoints;
		}

		prevComputedWorldPoints = new ArrayList<>();
		prevComputedWorldPoints.add(path.getLocations().get(0).toCenteredWorldPoint());
		for (int i = 1; i < path.getLocations().size(); i++)
		{
			final BrimhavenAgilityArenaLocation prev = path.getLocations().get(i - 1);
			final BrimhavenAgilityArenaLocation curr = path.getLocations().get(i);
			if (plankManager.getPlanks1Choice() != BrimhavenAgilityPlankChoice.UNKNOWN &&
				PLANKS_1_Y == prev.getY() && PLANKS_1_Y == curr.getY()
				&& PLANKS_1_X.contains(prev.getX()) && PLANKS_1_X.contains(curr.getX()))

			{
				addPlanksToWorldPoints(prevComputedWorldPoints, prev, curr, plankManager.getPlanks1Choice());
			}
			else if (plankManager.getPlanks2Choice() != BrimhavenAgilityPlankChoice.UNKNOWN &&
				PLANKS_2_Y == prev.getY() && PLANKS_2_Y == curr.getY()
				&& PLANKS_2_X.contains(prev.getX()) && PLANKS_2_X.contains(curr.getX()))
			{
				addPlanksToWorldPoints(prevComputedWorldPoints, prev, curr, plankManager.getPlanks2Choice());
			}
			prevComputedWorldPoints.add(curr.toCenteredWorldPoint());
		}
		return prevComputedWorldPoints;
	}

	private void addPlanksToWorldPoints(List<WorldPoint> out, BrimhavenAgilityArenaLocation prev, BrimhavenAgilityArenaLocation curr, BrimhavenAgilityPlankChoice planksChoice)
	{
		final boolean eastToWest = prev.getX() < curr.getX();
		final var point1 = prev.toCenteredWorldPoint()
			.dx((eastToWest ? 1 : -1) * 2);
		final var point2 = curr.toCenteredWorldPoint()
			.dx((eastToWest ? -1 : 1) * 2);
		out.add(point1);
		out.add(point1.dy(planksChoice.ordinal() - 1));
		out.add(point2.dy(planksChoice.ordinal() - 1));
		out.add(point2);
	}
}
