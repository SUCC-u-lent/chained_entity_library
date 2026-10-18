package com.ubunifu.multibossbar.common.boss_bar.sections;

import com.google.common.base.Objects;
import com.google.common.collect.*;
import com.ubunifu.multibossbar.common.boss_bar.SectionType;
import com.ubunifu.multibossbar.common.boss_bar.container.ServerMultiBossBar;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.entity.boss.*;
import net.minecraft.network.packet.s2c.play.BossBarUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.function.Function;

public class ServerBossBarSection extends BossBarSection {
	public ServerBossBarSection(int index, Text displayName, BossBar.Color color, BossBar.Style style, SectionType sectionType, int scaleSupplier) {
		super(index, MathHelper.randomUuid(), displayName, color, style, sectionType, scaleSupplier);
	}

	public void setPercent(float percentage) {
		if (percentage != this.percent) {
			super.setPercent(percentage);
			this.sendPacket(MultibossBarS2CPacket::updateProgress);
		}

	}

	public void setColor(BossBar.Color color) {
		if (color != this.color) {
			super.setColor(color);
			this.sendPacket(MultibossBarS2CPacket::updateColor);
		}

	}

	public void setStyle(BossBar.Style style) {
		if (style != this.style) {
			super.setStyle(style);
			this.sendPacket(MultibossBarS2CPacket::updateStyle);
		}

	}

	public BossBarSection setDarkenSky(boolean darkenSky) {
		if (darkenSky != this.darkenSky) {
			super.setDarkenSky(darkenSky);
			this.sendPacket(MultibossBarS2CPacket::updateDarkenSky);
		}

		return this;
	}

	public BossBarSection setDragonMusic(boolean dragonMusic) {
		if (dragonMusic != this.dragonMusic) {
			super.setDragonMusic(dragonMusic);
			this.sendPacket(MultibossBarS2CPacket::updateDragonMusic);
		}

		return this;
	}

	public BossBarSection setThickenFog(boolean thickenFog) {
		if (thickenFog != this.thickenFog) {
			super.setThickenFog(thickenFog);
			this.sendPacket(MultibossBarS2CPacket::updateThickenFog);
		}

		return this;
	}

	public void setName(Text name) {
		if (!Objects.equal(name, this.name)) {
			super.setName(name);
			this.sendPacket(MultibossBarS2CPacket::updateName);
		}

	}

	private void sendPacket(Function<ServerBossBarSection, MultibossBarS2CPacket> bossBarToPacketFunction, ServerPlayerEntity player) {
		((ServerMultiBossBar)this.getContainer()).sendPacket( bossBarToPacketFunction.apply(this), player );
	}
	private void sendPacket(Function<ServerBossBarSection, MultibossBarS2CPacket> bossBarToPacketFunction) {
		Set<ServerPlayerEntity> serverPlayerEntities = this.getPlayers();
		serverPlayerEntities.forEach(s->sendPacket(bossBarToPacketFunction, s));
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
