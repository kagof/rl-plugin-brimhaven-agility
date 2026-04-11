package com.kagof.runelite.plugins.brimhavenagility.overlay;

import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityConfig;
import com.kagof.runelite.plugins.brimhavenagility.BrimhavenAgilityPlugin;
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
		boolean nearAgilityArenaEntrance = plugin.isNearAgilityArenaEntrance();
		boolean inArena = plugin.isInAgilityArena();
		boolean wearingGloves = plugin.isWearingKaramjaGloves234();
		boolean hasCompletedDiary = plugin.isMediumDiaryCompleted();

		boolean entryPanel = config.showEntryPanel() && nearAgilityArenaEntrance;
		boolean gloveWarning = config.showGloveWarning() && hasCompletedDiary && !wearingGloves
			&& (nearAgilityArenaEntrance || inArena);

		if (entryPanel || gloveWarning)
		{
			panelComponent.getChildren().add(TitleComponent.builder()
				.text("Brimhaven Agility")
				.build());

			if (entryPanel)
			{
				boolean paid = plugin.isEntryPaid();
				boolean cooldownPassed = plugin.isCooldownPassed();
				boolean hasNoFollower = plugin.isHasNoFollower();
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
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Follower:")
					.right(hasNoFollower ? "No" : "Yes")
					.rightColor(hasNoFollower ? Color.GREEN : Color.RED)
					.build());
			}
			// if the panel is showing for any reason & the glove warning is enabled, add the glove info to it
			if (config.showGloveWarning())
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Diary gloves:")
					.right(wearingGloves ? "On" : hasCompletedDiary ? "Off" : "N/A")
					.rightColor(wearingGloves ? Color.GREEN : hasCompletedDiary ? Color.RED : Color.LIGHT_GRAY)
					.build());
			}
		}
		return super.render(graphics);
	}
}
