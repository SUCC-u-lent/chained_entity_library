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

	public ClientBossBarSection(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, SectionType sectionType, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
		super(uuid, name, color, style, sectionType);
		this.healthLatest = percent;
		this.percent = percent;
		this.timeHealthSet = Util.getMeasuringTimeMs();
		this.setDarkenSky(darkenSky);
		this.setDragonMusic(dragonMusic);
		this.setThickenFog(thickenFog);
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
