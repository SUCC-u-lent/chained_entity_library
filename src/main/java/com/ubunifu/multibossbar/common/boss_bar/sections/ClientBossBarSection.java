package com.ubunifu.multibossbar.common.boss_bar.sections;

import com.ubunifu.multibossbar.common.boss_bar.SectionType;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class ClientBossBarSection extends BossBarSection {
	protected float healthLatest;
	protected long timeHealthSet;

	public ClientBossBarSection(int index, UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, SectionType sectionType, boolean darkenSky, boolean dragonMusic, boolean thickenFog, int scaleSupplier) {
		super(index, uuid, name, percent, color, style, sectionType, darkenSky, dragonMusic, thickenFog, scaleSupplier);
		this.healthLatest = percent;
		this.timeHealthSet = Util.getMeasuringTimeMs();
	}

	public ClientBossBarSection(int index, UUID uuid, Text name, BossBar.Color color, BossBar.Style style, SectionType sectionType, int scaleSupplier) {
		super(index, uuid, name, color, style, sectionType, scaleSupplier);
		this.healthLatest = 1;
		this.timeHealthSet = Util.getMeasuringTimeMs();
	}

	public ClientBossBarSection(UUID uuid, Text name, BossBar.Color color, BossBar.Style style, SectionType sectionType) {
		super(uuid, name, color, style, sectionType);
		this.healthLatest = 1;
		this.timeHealthSet = Util.getMeasuringTimeMs();
	}

	public void setPercent(float percentage) {
		this.percent = this.getPercent();
		this.healthLatest = percentage;
		this.timeHealthSet = Util.getMeasuringTimeMs();
	}

	public float getPercent() {
		long l = Util.getMeasuringTimeMs() - this.timeHealthSet;
		float f = MathHelper.clamp((float)l / 100.0F, 0.0F, 1.0F);
		return MathHelper.lerp(f, this.percent, this.healthLatest);
	}
}
