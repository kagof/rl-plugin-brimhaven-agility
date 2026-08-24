package com.kagof.runelite.plugins.brimhavenagility;

import static com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityNotifier.buildDispenserDistanceNotifyString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BrimhavenAgilityNotifierTest
{
	@Test
	public void TestNotificationString()
	{
		assertEquals("Brimhaven Agility: active ticket dispenser is on your platform",
			buildDispenserDistanceNotifyString(0));
		assertEquals("Brimhaven Agility: active ticket dispenser is 1 platform hop away",
			buildDispenserDistanceNotifyString(1));
		assertEquals("Brimhaven Agility: active ticket dispenser is 10 platform hops away",
			buildDispenserDistanceNotifyString(10));
	}
}
