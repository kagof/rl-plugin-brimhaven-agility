package com.kagof.runelite.plugins.brimhavenagility.overlay;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPlugin;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityWorldPathConverter;
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
	private final BrimhavenAgilityWorldPathConverter worldPathConverter;

	@Inject
	public BrimhavenAgilityOverlay(final BrimhavenAgilityPlugin plugin, final BrimhavenAgilityConfig config,
								   final BrimhavenAgilityWorldPathConverter worldPathConverter)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
		this.worldPathConverter = worldPathConverter;
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (!config.drawPath() || !plugin.isInAgilityArena() || !plugin.isTicketAvailable())
		{
			return null;
		}
		BrimhavenAgilityArenaPath currentPath = plugin.getCurrentPath();
		if (currentPath != null && currentPath.size() > 1)
		{
			WorldLines.drawLinesOnWorld(graphics2D,
				plugin.getClient(),
				worldPathConverter.toWorldPoints(currentPath),
				config.pathColour());
		}
		return null;
	}
}
