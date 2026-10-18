package com.ubunifu.multibossbar.common.boss_bar.sections.framework;

import com.ubunifu.multibossbar.common.boss_bar.*;
import com.ubunifu.multibossbar.common.boss_bar.container.MultiBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.util.*;

/** A boss bar section is an individual boss bar, it closely replicates the base-game one. */
public class BossBarSection
{
	public final UUID uuid;
	public Text name;
	public float percent;
	public BossBar.Color color;
	public BossBar.Style style;
	public boolean darkenSky;
	public boolean dragonMusic;
	public boolean thickenFog;
	public SectionType sectionType;
	private MultiBossBar container;
	// Vertical scale of the minibar, where 5 is the default value. This is used to determine the height of the minibar on the screen.
	int scaleSupplier;
	private int barIndex;


	public void serializeToBuf(PacketByteBuf buf)
	{
		buf.writeUuid( uuid );
		buf.writeInt( barIndex );
		TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode(buf, name);
		buf.writeFloat(percent);
		buf.writeEnumConstant(color);
		buf.writeEnumConstant(style);
		buf.writeEnumConstant(sectionType);
		buf.writeBoolean(darkenSky);
		buf.writeBoolean(dragonMusic);
		buf.writeBoolean(thickenFog);
		buf.writeInt(scaleSupplier);
	}
	public static BossBarSection deserializeFromBuf(PacketByteBuf buf)
	{
		UUID uuid = buf.readUuid();
		int index = buf.readInt();
		Text text = TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode( buf );
		float percent = buf.readFloat();
		BossBar.Color color = buf.readEnumConstant(BossBar.Color.class);
		BossBar.Style style = buf.readEnumConstant(BossBar.Style.class);
		SectionType sectionType = buf.readEnumConstant(SectionType.class);
		boolean darkenSky = buf.readBoolean();
		boolean dragonMusic = buf.readBoolean();
		boolean thickenFog = buf.readBoolean();
		int scaleSupplier = buf.readInt();
		return new BossBarSection( index, uuid, text, percent, color, style, sectionType, darkenSky, dragonMusic, thickenFog, scaleSupplier );
	}

	private BossBarSection(int index,
						   UUID uuid,
	                       Text name,
	                       float percent,
	                       BossBar.Color color,
	                       BossBar.Style style,
	                       SectionType sectionType,
						   boolean darkenSky,
						   boolean dragonMusic,
						   boolean thickenFog,
	                       int scaleSupplier){
		this.barIndex = index;
		this.uuid = uuid;
		this.name = name;
		this.color = color;
		this.style = style;
		this.sectionType = sectionType;
		this.scaleSupplier = scaleSupplier;
		this.percent = percent;
		this.darkenSky = darkenSky;
		this.dragonMusic = dragonMusic;
		this.thickenFog = thickenFog;
	}

	public BossBarSection(int index,UUID uuid, Text name, BossBar.Color color, BossBar.Style style, SectionType sectionType, int scaleSupplier) {
		this(index,uuid,name,1,color,style,sectionType, false, false, false ,scaleSupplier);
	}

	public BossBarSection(UUID uuid, Text name, BossBar.Color color, BossBar.Style style, SectionType sectionType) {
		this(0, uuid, name, 1.0F, color, style, sectionType, false, false, false, 5);
	}

	public MultiBossBar getContainer() { return this.container; }
	public void setContainer( MultiBossBar bossBar ) { this.container = bossBar; }

	public int getScaleSupplier() {
		return this.scaleSupplier;
	}

	public int getBarIndex() { return this.barIndex; }
	public void setBarIndex(int barIndex) { this.barIndex = barIndex; }

	/**
	 * Sets the vertical scale of the minibar, where 5 is the default value. This is used to determine the height of the minibar on the screen.
	 * @param scaleSupplier A supplier that provides the vertical scale of the minibar. Must not be null.
	 */
	public void setScaleSupplier(int scaleSupplier) {
		this.scaleSupplier = scaleSupplier;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public SectionType getSectionType() { return this.sectionType; }

	public void setSectionType(SectionType sectionType) { this.sectionType = sectionType; }

	public Text getName() {
		return this.name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(float percentage) {
		this.percent = percentage;
	}

	public BossBar.Color getColor() {
		return this.color;
	}

	public void setColor(BossBar.Color color) {
		this.color = color;
	}

	public BossBar.Style getStyle() {
		return this.style;
	}

	public void setStyle(BossBar.Style style) {
		this.style = style;
	}

	public boolean shouldDarkenSky() {
		return this.darkenSky;
	}

	public BossBarSection setDarkenSky(boolean darkenSky) {
		this.darkenSky = darkenSky;
		return this;
	}

	public boolean hasDragonMusic() {
		return this.dragonMusic;
	}

	public BossBarSection setDragonMusic(boolean dragonMusic) {
		this.dragonMusic = dragonMusic;
		return this;
	}

	public BossBarSection setThickenFog(boolean thickenFog) {
		this.thickenFog = thickenFog;
		return this;
	}

	public boolean shouldThickenFog() {
		return this.thickenFog;
	}
}
