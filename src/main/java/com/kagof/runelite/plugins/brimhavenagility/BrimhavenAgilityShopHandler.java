package com.kagof.runelite.plugins.brimhavenagility;

import java.awt.Rectangle;
import net.runelite.api.Client;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

public class BrimhavenAgilityShopHandler
{
	public static final int FRAME_ID = InterfaceID.OmnishopMain.FRAME;
	public static final int LIST_ID = InterfaceID.OmnishopMain.LIST;
	public static final int INFO_ID = InterfaceID.OmnishopMain.INFO;
	public static final int BUY_BUTTONS_ID = InterfaceID.OmnishopMain.BUTTONS_INFO;

	public static Widget getFrame(final Client client)
	{
		return client.getWidget(FRAME_ID);
	}

	public static Widget getList(final Client client)
	{
		return client.getWidget(LIST_ID);
	}

	public static Widget getInfo(final Client client)
	{
		return client.getWidget(INFO_ID);
	}

	public static Widget getBuyButtons(final Client client)
	{
		return client.getWidget(BUY_BUTTONS_ID);
	}

	public static boolean shopNameMatches(final Client client)
	{
		final Widget frame = getFrame(client);
		if (frame == null)
		{
			return false;
		}
		final Widget[] frameChildren = frame.getChildren();
		if (frameChildren == null)
		{
			return false;
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

	public static boolean isAgilityXPOptionSelected(final Widget info)
	{
		if (info == null)
		{
			return false;
		}
		var infoChildren = info.getChildren();
		if (infoChildren == null)
		{
			return false;
		}
		for (var infoChild : infoChildren)
		{
			if (infoChild.getText() != null && infoChild.getText().contains("Agility XP"))
			{
				return true;
			}
		}
		return false;
	}

	public static Rectangle agilityXPBuyButtonBounds(final Client client)
	{
		final Widget info = getInfo(client);
		final Widget buyButtons = getBuyButtons(client);
		if (info == null || buyButtons == null)
		{
			return null;
		}
		Rectangle buyButtonBounds = buyButtons.getBounds();
		if (buyButtonBounds.x < 0 || buyButtonBounds.y < 0)
		{
			return null;
		}
		if (isAgilityXPOptionSelected(info))
		{
			return buyButtonBounds;
		}
		return null;
	}

	public static Rectangle getAgilityXPListItemBounds(final Client client)
	{
		final Widget list = getList(client);
		if (list == null)
		{
			return null;
		}
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

	public static boolean isAgilityXPBuyButtonMenuOption(final MenuEntryAdded event, final Client client)
	{
		return "".equals(event.getTarget())
			&& event.getOption().contains("Buy-")
			&& isAgilityXPOptionSelected(getInfo(client));
	}

	public static boolean isAgilityXPListBuyOrSelectMenuOption(final MenuEntryAdded event)
	{
		return event.getTarget().contains("Agility XP")
			&& ("Select".equals(event.getOption()) || event.getOption().contains("Buy-"));
	}
}
