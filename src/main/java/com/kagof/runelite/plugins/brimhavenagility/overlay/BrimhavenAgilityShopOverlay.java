package com.kagof.runelite.plugins.brimhavenagility.overlay;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPlugin;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityShopHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

public class BrimhavenAgilityShopOverlay extends Overlay
{
	private final BrimhavenAgilityPlugin plugin;
	private final BrimhavenAgilityConfig config;
	private final Client client;

	private Rectangle listItemBounds = null;
	private Rectangle buyButtonsBounds = null;

	@Inject
	public BrimhavenAgilityShopOverlay(final BrimhavenAgilityPlugin plugin, final BrimhavenAgilityConfig config, final Client client)
	{
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.plugin = plugin;
		this.config = config;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics2D)
	{
		if (!plugin.isShopOpen() || !plugin.isNearAgilityArenaEntrance() || !config.showGloveWarning() || plugin.isWearingKaramjaGloves234())
		{
			if (listItemBounds != null || buyButtonsBounds != null)
			{
				listItemBounds = null;
				buyButtonsBounds = null;
			}
			return null;
		}

		recomputeShopRects();
		drawWarningIfNonNull(graphics2D, listItemBounds);
		drawWarningIfNonNull(graphics2D, buyButtonsBounds);
		return null;
	}

	private void recomputeShopRects()
	{
		final Widget frame = client.getWidget(BrimhavenAgilityShopHandler.FRAME_ID);
		final Widget list = client.getWidget(BrimhavenAgilityShopHandler.LIST_ID);
		final Widget info = client.getWidget(BrimhavenAgilityShopHandler.INFO_ID);
		final Widget buyButtons = client.getWidget(BrimhavenAgilityShopHandler.BUY_BUTTONS_ID);

		if (!BrimhavenAgilityShopHandler.shopNameMatches(frame))
		{
			listItemBounds = null;
			buyButtonsBounds = null;
			return;
		}
		listItemBounds = BrimhavenAgilityShopHandler.getAgilityXPListItemBounds(list);
		buyButtonsBounds = BrimhavenAgilityShopHandler.agilityXPBuyButtonBounds(info, buyButtons);
	}

	private void drawWarningIfNonNull(Graphics2D graphics2D, Rectangle rectangle)
	{
		if (rectangle != null)
		{
			graphics2D.setColor(Color.RED);
			graphics2D.draw(rectangle);
			graphics2D.setColor(ColorUtil.colorWithAlpha(Color.RED, 127));
			graphics2D.fill(rectangle);
			OverlayUtil.renderTextLocation(graphics2D, new Point(rectangle.x, rectangle.y + rectangle.height), "Equip Karamja gloves!", Color.WHITE);
		}
	}

}
