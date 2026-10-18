package com.ubunifu.multibossbar.common.boss_bar.container;

import com.ubunifu.multibossbar.common.boss_bar.FillType;
import com.ubunifu.multibossbar.common.boss_bar.sections.*;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Text;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;

public class MultiBossBar {
	UUID uuid;
	private final List<BossBarSection> multiBossBars;
	private FillType fillType;
	boolean visible;
	private MultiBossBar(UUID uuid, List<BossBarSection> bossBarSections, FillType fillType)
	{
		this.uuid = uuid;
		this.multiBossBars = bossBarSections;
		this.fillType = fillType;
	}
	public MultiBossBar(List<BossBarSection> bossBarSections, FillType fillType)
	{
		this(UUID.randomUUID(), bossBarSections, fillType);
	}

	public MultiBossBar() {
		this(new ArrayList<>(), FillType.VANILLA);
	}

	public List<BossBarSection> getBossBars() {
		return List.copyOf( this.multiBossBars );
	}

	public List<BossBarSection> getMinibars() {
		return this.getBossBars();
	}

	public Text getDisplayName() {
		if (this.multiBossBars.isEmpty()) {
			return Text.empty();
		}
		return this.multiBossBars.get(0).getName();
	}

	public BossBarSection get(int index) {
		if (isOutOfBounds(index)) return null;
		return this.multiBossBars.get(index);
	}

	public int getCount() {
		return this.count();
	}

	public boolean isDarkenSky() {
		return this.multiBossBars.stream().anyMatch(BossBarSection::shouldDarkenSky);
	}

	public boolean isDragonMusic() {
		return this.multiBossBars.stream().anyMatch(BossBarSection::hasDragonMusic);
	}

	public boolean isThickenFog() {
		return this.multiBossBars.stream().anyMatch(BossBarSection::shouldThickenFog);
	}
	public void setBossBars(List<BossBarSection> sections) {
		this.multiBossBars.clear();
		this.multiBossBars.addAll( sections );
	}

	public UUID getUuid() { return this.uuid; }

	public FillType getFillType() {
		return this.fillType;
	}
	public void setFillType(FillType fillType){
		this.fillType = fillType;
	}

	public void addBossBar(BossBarSection minibar) {
		int index = this.multiBossBars.size();
		minibar.setBarIndex( index );
		this.multiBossBars.add(minibar);
	}

	public void removeBossBar(int index) {
		this.multiBossBars.remove( index );
	}

	public int count() {
		return this.multiBossBars.size();
	}

	private boolean isOutOfBounds(int index)
	{
		return index < 0 || index >= multiBossBars.size();
	}

	public void setDisplayName(int index, Text text) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setName( text );
		this.multiBossBars.set( index, b );
	}

	public void setColor(int index, BossBar.Color color) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setColor( color );
		this.multiBossBars.set( index, b );
	}

	public void setPercent(int index, float progress) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setPercent( progress );
		this.multiBossBars.set( index, b );
	}

	public void setStyle(int index, BossBar.Style style) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setStyle( style );
		this.multiBossBars.set( index, b );
	}

	public void setDarkenSky(int index, boolean darkenSky) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setDarkenSky( darkenSky );
		this.multiBossBars.set( index, b );
	}

	public void setDragonMusic(int index, boolean dragonMusic) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setDragonMusic( dragonMusic );
		this.multiBossBars.set( index, b );
	}

	public void setThickenFog(int index, boolean thickenFog) {
		if (isOutOfBounds(index)) return;
		BossBarSection b = multiBossBars.get( index );
		b.setThickenFog( thickenFog );
		this.multiBossBars.set( index, b );
	}

	public void clearBars() {
		this.multiBossBars.clear();
	}

	public void replaceBar(@NotNull ClientBossBarSection section) {
		if (this.multiBossBars.removeIf(b->b.getUuid().equals(section.getUuid())))
			this.multiBossBars.add(section);
	}

	public void setBossBar(int index, BossBarSection section) {
		this.multiBossBars.set(index,section);
	}


	public boolean isVisible() { return this.visible; }
	public void setVisible( boolean visible ) {
		this.visible = visible;
	}
}
