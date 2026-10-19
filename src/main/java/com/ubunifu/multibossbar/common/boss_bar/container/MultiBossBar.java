package com.ubunifu.multibossbar.common.boss_bar.container;

import com.ubunifu.multibossbar.common.boss_bar.FillType;
import com.ubunifu.multibossbar.common.boss_bar.sections.*;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;

public class MultiBossBar {
	UUID uuid;
	Text name;
	private final List<BossBarSection> multiBossBars;
	private FillType fillType;
	boolean visible;

	public void writeToBuffer(PacketByteBuf p) {
		p.writeUuid(uuid);
		TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode(p, name);
		p.writeInt(multiBossBars.size());
		for (BossBarSection bar : multiBossBars)
			bar.serializeToBuf(p);
		p.writeEnumConstant(fillType);
		p.writeBoolean(visible);
	}
	public static ClientMultiBossBar client_deserializeFromBuffer(PacketByteBuf p) {
		UUID uuid = p.readUuid();
		Text name = TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode(p);
		int listSize = p.readInt();
		List<BossBarSection> sections = new ArrayList<>(listSize);
		for (int i = 0; i < listSize; i++) {
			BossBarSection section = BossBarSection.client_deserializeFromBuf(p);
			sections.add(section);
		}
		FillType fillType = p.readEnumConstant(FillType.class);
		boolean visible = p.readBoolean();
		return new ClientMultiBossBar(uuid,name,sections,fillType,visible);
	}

	protected MultiBossBar(UUID uuid, Text name, List<BossBarSection> sections, FillType fillType, boolean visible) {
		this.uuid = uuid;
		this.name = name;
		this.multiBossBars = sections;
		this.fillType = fillType;
		this.visible = visible;
	}
	public MultiBossBar(UUID uuid, List<BossBarSection> bossBarSections, FillType fillType)
	{
		this(uuid, Text.empty(), bossBarSections, fillType, false);
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
		return this.name;
	}

	public void setDisplayName(Text text) {
		this.name = text;
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

	public boolean isIndexOccupied(int index) {
		return index >= 0 && this.multiBossBars.size() < index;
	}

	public void setBossBar(int index, BossBarSection section) {
		if (index < this.multiBossBars.size())
			this.multiBossBars.set(index,section);
		else throw new NoSuchElementException();
	}


	public boolean isVisible() { return this.visible; }
	public void setVisible( boolean visible ) {
		this.visible = visible;
	}
}
