package com.kagof.runelite.plugins.brimhavenagility.model;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class BrimhavenAgilityArenaPathTest
{
	@Test
	public void emptyPathHasUnknownDistance()
	{
		Assert.assertEquals(-1, new BrimhavenAgilityArenaPath(List.of()).distance());
	}

	@Test
	public void samePlatformHasZeroDistance()
	{
		BrimhavenAgilityArenaPath path = new BrimhavenAgilityArenaPath(List.of(
			BrimhavenAgilityArenaLocation.of(0, 0)));

		Assert.assertEquals(0, path.distance());
	}

	@Test
	public void distanceCountsPlatformHops()
	{
		BrimhavenAgilityArenaPath path = new BrimhavenAgilityArenaPath(List.of(
			BrimhavenAgilityArenaLocation.of(0, 0),
			BrimhavenAgilityArenaLocation.of(1, 0),
			BrimhavenAgilityArenaLocation.of(2, 0)));

		Assert.assertEquals(2, path.distance());
	}
}
