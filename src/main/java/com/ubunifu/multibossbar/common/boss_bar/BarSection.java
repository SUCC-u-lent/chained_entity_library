package com.ubunifu.multibossbar.common.boss_bar;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;

import java.util.UUID;

public class BarSection {
	final MultibarContainer container;
	final UUID uuid;
	protected Text name;
	protected double percentage;
	protected BossBar.Color color;
	protected BossBar.Style style;
	protected float verticalScale;

	protected BarSection(MultibarContainer container, UUID uuid) {
		this.uuid = uuid;
		this.container = container;
		onConstructed();
	}
	protected BarSection(MultibarContainer container, UUID uuid,Text name, double percentage, BossBar.Color color, BossBar.Style style, float verticalScale) {
		this.uuid = uuid;
		this.container = container;
		this.name = name;
		this.percentage = percentage;
		this.color = color;
		this.style = style;
		this.verticalScale = verticalScale;
		onConstructed();
	}

	protected void onConstructed(){}

	public Text getName() {
		return name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public BossBar.Color getColor() {
		return color;
	}

	public void setColor(BossBar.Color color) {
		this.color = color;
	}

	public BossBar.Style getStyle() {
		return style;
	}

	public void setStyle(BossBar.Style style) {
		this.style = style;
	}

	public MultibarContainer getContainer() {
		return container;
	}

	public UUID getUuid() {
		return uuid;
	}

	public float getVerticalScale() {
		return verticalScale;
	}

	public void setVerticalScale(float verticalScale) {
		this.verticalScale = verticalScale;
	}
}
