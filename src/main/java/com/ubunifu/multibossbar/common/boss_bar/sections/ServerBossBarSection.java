package com.ubunifu.multibossbar.common.boss_bar.sections;

import com.google.common.base.Objects;
import com.ubunifu.multibossbar.common.boss_bar.*;
import com.ubunifu.multibossbar.common.boss_bar.container.*;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import net.minecraft.entity.boss.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class ServerBossBarSection extends BossBarSection {
	public ServerBossBarSection(int index, Text displayName, BossBar.Color color, BossBar.Style style, SectionType sectionType, int scaleSupplier) {
		super(index, MathHelper.randomUuid(), displayName, color, style, sectionType, scaleSupplier);
	}

	private void issueUpdate(PacketType type)
	{
		ServerMultiBossBar container = (ServerMultiBossBar) this.getContainer();
		container.onBarUpdate(type, this);
	}

	public void setPercent(float percentage) {
		if (percentage != this.percent) {
			super.setPercent(percentage);
			issueUpdate(PacketType.UPDATE_PERCENT);
		}
	}

	public void setColor(BossBar.Color color) {
		if (color != this.color) {
			super.setColor(color);
			issueUpdate(PacketType.UPDATE_COLOR);
		}

	}

	public void setStyle(BossBar.Style style) {
		if (style != this.style) {
			super.setStyle(style);
			issueUpdate(PacketType.UPDATE_STYLE);
		}
	}

	@Override
	public void setScaleSupplier(int scaleSupplier) {
		int previousScale = this.getScaleSupplier();
		super.setScaleSupplier(scaleSupplier);
		if (previousScale != scaleSupplier)
		{
			issueUpdate(PacketType.SET_BAR_SCALE);
		}
	}

	public BossBarSection setDarkenSky(boolean darkenSky) {
		if (darkenSky != this.darkenSky) {
			super.setDarkenSky(darkenSky);
			issueUpdate(PacketType.UPDATE_DARKEN_SKY);
		}

		return this;
	}

	public BossBarSection setDragonMusic(boolean dragonMusic) {
		if (dragonMusic != this.dragonMusic) {
			super.setDragonMusic(dragonMusic);
			issueUpdate(PacketType.UPDATE_DRAGON_MUSIC);
		}

		return this;
	}

	public BossBarSection setThickenFog(boolean thickenFog) {
		if (thickenFog != this.thickenFog) {
			super.setThickenFog(thickenFog);
			issueUpdate(PacketType.UPDATE_THICKEN_FOG);
		}

		return this;
	}

	public void setName(Text name) {
		if (!Objects.equal(name, this.name)) {
			super.setName(name);
			issueUpdate(PacketType.UPDATE_NAME);
		}
	}

	@Override
	public void setSectionType(SectionType sectionType) {
		SectionType type = this.getSectionType();
		super.setSectionType(sectionType);
		if (type != sectionType)
			issueUpdate(PacketType.UPDATE_SECTION_TYPE);
	}

	private Set<ServerPlayerEntity> getPlayers() {
		return ((ServerMultiBossBar)this.getContainer()).getPlayers();
	}

	public boolean isVisible() {
		return ((ServerMultiBossBar)this.getContainer()).isVisible();
	}

	public void setVisible(boolean visible) {
		boolean vis = this.isVisible();
		if (visible != vis) {
			((ServerMultiBossBar) this.getContainer()).setVisible(visible);
		}
	}
}
