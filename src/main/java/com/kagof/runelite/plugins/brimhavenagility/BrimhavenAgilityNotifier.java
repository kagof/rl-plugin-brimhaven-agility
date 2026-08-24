package com.kagof.runelite.plugins.brimhavenagility;

import com.google.common.annotations.VisibleForTesting;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaLocation;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaPath;
import javax.inject.Inject;
import net.runelite.client.Notifier;
import net.runelite.client.config.Notification;

public class BrimhavenAgilityNotifier
{
	private static final int NOTIFICATION_MAX_LEN = 67;

	private final Notifier notifier;
	private volatile BrimhavenAgilityArenaLocation lastDispenserLocation = null;

	@Inject
	public BrimhavenAgilityNotifier(final Notifier notifier)
	{
		this.notifier = notifier;
	}

	public void maybeNotifyDispenserDistance(final BrimhavenAgilityArenaPath currentPath, final BrimhavenAgilityConfig config)
	{
		if (!config.nearbyDispenserNotification().isEnabled())
		{
			return;
		}
		if (currentPath == null || currentPath.distance() < 0)
		{
			return;
		}
		if (currentPath.end().equals(lastDispenserLocation))
		{
			return;
		}
		// Update the last dispenser location regardless of whether we notified for it.
		// This means that we won't notify when the player moves closer, only if the dispenser moves.
		lastDispenserLocation = currentPath.end();
		if (currentPath.distance() > config.nearbyDispenserDistance())
		{
			return;
		}
		notify(config.nearbyDispenserNotification(), buildDispenserDistanceNotifyString(currentPath.distance()));

	}

	public void clear()
	{
		lastDispenserLocation = null;
	}

	private void notify(final Notification notification, final String message)
	{
		notifier.notify(notification, message);
	}

	@VisibleForTesting
	static String buildDispenserDistanceNotifyString(final int dispenserDistance)
	{
		StringBuilder sb = new StringBuilder(NOTIFICATION_MAX_LEN);
		sb.append("Brimhaven Agility: active ticket dispenser is ");
		if (dispenserDistance == 0)
		{
			sb.append("on your platform");
		}
		else
		{
			sb.append(dispenserDistance)
				.append(" platform hop");
			if (dispenserDistance > 1)
			{
				sb.append("s");
			}
			sb.append(" away");
		}
		return sb.toString();
	}
}
