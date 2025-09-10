package com.kagof.runelite.plugins.brimhavenagility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class BrimhavenAgilityPanelOverlay extends OverlayPanel
{
	private final BrimhavenAgilityPlugin plugin;
	private final BrimhavenAgilityConfig config;

	@Inject
	public BrimhavenAgilityPanelOverlay(final BrimhavenAgilityPlugin plugin, final BrimhavenAgilityConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		setMovable(true);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.showEntryPanel() && plugin.isNearAgilityArenaEntrance())
		{
			boolean paid = plugin.isEntryPaid();
			boolean cooldownPassed = plugin.isCooldownPassed();
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Brimhaven Agility")
				.build());

			panelComponent.getChildren().add(LineComponent.builder()
				.left("Entry fee:")
				.right(paid ? "Paid" : "Not Paid")
				.rightColor(paid ? Color.GREEN : Color.RED)
				.build());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Cooldown:")
				.right(cooldownPassed ? "Over" : "Active")
				.rightColor(cooldownPassed ? Color.GREEN : Color.RED)
				.build());
		}
		return super.render(graphics);
	}
}
