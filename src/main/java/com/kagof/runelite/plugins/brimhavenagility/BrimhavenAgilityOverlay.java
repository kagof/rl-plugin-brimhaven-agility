package com.kagof.runelite.plugins.brimhavenagility;

import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaPath;
import com.kagof.runelite.plugins.brimhavenagility.questhelper.WorldLines;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class BrimhavenAgilityOverlay extends Overlay
{
	private final BrimhavenAgilityPlugin plugin;
	private final BrimhavenAgilityConfig config;

	@Inject
	public BrimhavenAgilityOverlay(BrimhavenAgilityPlugin plugin, BrimhavenAgilityConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (!plugin.isInAgilityArena() || !plugin.isTicketAvailable())
		{
			return null;
		}
		BrimhavenAgilityArenaPath currentPath = plugin.getCurrentPath();
		if (currentPath != null && currentPath.size() > 1)
		{
			WorldLines.drawLinesOnWorld(graphics2D, plugin.getClient(), currentPath.toWorldPoints(), config.pathColour());
		}
		return null;
	}
}
