package com.kagof.runelite.plugins.brimhavenagility;

import java.awt.Rectangle;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

public class BrimhavenAgilityShopHandler
{
	public static final int FRAME_ID = InterfaceID.OmnishopMain.FRAME;
	public static final int LIST_ID = InterfaceID.OmnishopMain.LIST;
	public static final int INFO_ID = InterfaceID.OmnishopMain.INFO;
	public static final int BUY_BUTTONS_ID = InterfaceID.OmnishopMain.BUTTONS_INFO;

	public static boolean shopNameMatches(Widget frame)
	{
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

	public static boolean isAgilityXPOptionSelected(Widget info)
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

	public static Rectangle agilityXPBuyButtonBounds(Widget info, Widget buyButtons)
	{
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

	public static Rectangle getAgilityXPListItemBounds(Widget list)
	{
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

}
