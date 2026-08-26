package com.kagof.runelite.plugins.brimhavenagility.overlay;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
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
		if (listItemBounds != null)
		{
			graphics2D.setColor(Color.RED);
			graphics2D.draw(listItemBounds);
			graphics2D.setColor(ColorUtil.colorWithAlpha(Color.RED, 127));
			graphics2D.fill(listItemBounds);
			OverlayUtil.renderTextLocation(graphics2D, new Point(listItemBounds.x, listItemBounds.y + listItemBounds.height), "Equip Karamja gloves!", Color.WHITE);
		}
		if (buyButtonsBounds != null)
		{
			graphics2D.setColor(Color.RED);
			graphics2D.draw(buyButtonsBounds);
			graphics2D.setColor(ColorUtil.colorWithAlpha(Color.RED, 127));
			graphics2D.fill(buyButtonsBounds);
			OverlayUtil.renderTextLocation(graphics2D, new Point(buyButtonsBounds.x, buyButtonsBounds.y + buyButtonsBounds.height), "Equip Karamja gloves!", Color.WHITE);
		}
		return null;
	}

	private void recomputeShopRects()
	{
		final Widget frame = client.getWidget(InterfaceID.OmnishopMain.FRAME);
		final Widget list = client.getWidget(InterfaceID.OmnishopMain.LIST);
		final Widget info = client.getWidget(InterfaceID.OmnishopMain.INFO);
		final Widget buyButtons = client.getWidget(InterfaceID.OmnishopMain.BUTTONS_INFO);

		if (frame == null || list == null || buyButtons == null || info == null)
		{
			listItemBounds = null;
			buyButtonsBounds = null;
			return;
		}
		if (!shopNameMatches(frame))
		{
			listItemBounds = null;
			buyButtonsBounds = null;
			return;
		}
		listItemBounds = getAgilityXPListItemBounds(list);
		buyButtonsBounds = agilityXPBuyButtonBounds(info, buyButtons);
	}


	private Rectangle agilityXPBuyButtonBounds(Widget info, Widget buyButtons)
	{
		Rectangle buyButtonBounds = buyButtons.getBounds();
		if (buyButtonBounds.x < 0 || buyButtonBounds.y < 0)
		{
			return null;
		}
		var infoChildren = info.getChildren();
		if (infoChildren == null)
		{
			return null;
		}
		for (var infoChild : infoChildren)
		{
			if (infoChild.getText() != null && infoChild.getText().contains("Agility XP"))
			{
				return buyButtonBounds;
			}
		}
		return null;
	}

	private Rectangle getAgilityXPListItemBounds(Widget list)
	{
		var listChildren = list.getChildren();
		if (listChildren == null)
		{
			return null;
		}
		for (var listChild : listChildren)
		{
			if (listChild.getName() != null && listChild.getName().contains("Agility XP"))
			{
				Rectangle bounds = listChild.getBounds();
				if (bounds != null && (bounds.x == -1 || bounds.y == -1))
				{
					return null;
				}
				return bounds;
			}
		}
		return null;
	}

	private boolean shopNameMatches(Widget frame)
	{
		final Widget[] frameChildren = frame.getChildren();
		if (frameChildren == null)
		{
			return true;
		}
		for (var frameChild : frameChildren)
		{
			if (frameChild.getText() != null && frameChild.getText().contains("Agility Arena Store"))
			{
				return true;
			}
		}
		return false;
	}
}
