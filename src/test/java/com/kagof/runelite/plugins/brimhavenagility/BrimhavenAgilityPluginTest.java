package com.kagof.runelite.plugins.brimhavenagility;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BrimhavenAgilityPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BrimhavenAgilityPlugin.class);
		RuneLite.main(args);
	}
}