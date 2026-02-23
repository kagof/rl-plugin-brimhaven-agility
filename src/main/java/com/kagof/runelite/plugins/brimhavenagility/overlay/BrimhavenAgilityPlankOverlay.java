package com.kagof.runelite.plugins.brimhavenagility.overlay;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPlugin;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityArenaLocation;
import com.kagof.runelite.plugins.brimhavenagility.model.BrimhavenAgilityPlankChoice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GroundObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

public class BrimhavenAgilityPlankOverlay extends Overlay
{
	private static final int MAX_DISTANCE = 2350;

	private static final Set<Integer> notAllowed = Set.of(ObjectID.AGILITYARENA_PLANK_BROKE1);


	private final Client client;
	private final BrimhavenAgilityPlugin plugin;
	private final BrimhavenAgilityConfig config;

	@Inject
	public BrimhavenAgilityPlankOverlay(final Client client,
										final BrimhavenAgilityPlugin plugin,
										final BrimhavenAgilityConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics2D)
	{
		if (!plugin.isInAgilityArena() || !config.highlightCorrectPlank())
		{
			return null;
		}
		if (client.getLocalPlayer().getWorldLocation().getPlane() != BrimhavenAgilityArenaLocation.PLANE)
		{
			return null;
		}

		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

		if (plugin.getPlankManager().getPlanks1Choice() != BrimhavenAgilityPlankChoice.UNKNOWN)
		{
			render(graphics2D, playerLocation, plugin.getPlankManager().getPlanks1Choice(), plugin.getPlankManager().getPlanks1());
		}
		if (plugin.getPlankManager().getPlanks2Choice() != BrimhavenAgilityPlankChoice.UNKNOWN)
		{
			render(graphics2D, playerLocation, plugin.getPlankManager().getPlanks2Choice(), plugin.getPlankManager().getPlanks2());
		}
		return null;
	}

	private void render(final Graphics2D graphics2D, final LocalPoint playerLocation, BrimhavenAgilityPlankChoice choice, final GroundObject[][] planks)
	{
		for (int y = 0; y < planks.length; y++)

		{
			for (int x = 0; x < planks[y].length; x++)
			{
				if (planks[y][x] == null)
				{
					continue;
				}
				if (!config.highlightEntirePlank() && x != 0 && x != (planks[y].length - 1))
				{
					continue;
				}
				if (planks[y][x].getLocalLocation().distanceTo(playerLocation) >= MAX_DISTANCE)
				{
					continue;
				}
				Color color = y == choice.ordinal() ? config.correctPlankColour() : config.incorrectPlankColour();
				Shape clickbox = planks[y][x].getClickbox();
				if (clickbox != null)
				{
					graphics2D.setColor(color);
					graphics2D.draw(clickbox);
					graphics2D.setColor(ColorUtil.colorWithAlpha(color, color.getAlpha() / 5));
					graphics2D.fill(clickbox);
				}
				else
				{
					Polygon poly = planks[y][x].getCanvasTilePoly();
					if (poly != null)
					{
						OverlayUtil.renderPolygon(graphics2D, poly, color);
					}
				}
			}
		}
	}
}
