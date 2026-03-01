package com.ubunifu.multibossbar.common.barhandler;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Minibar
{
	@Nullable Text displayText;
	@NotNull BossBar.Color color;
	@NotNull BossBar.Style style;
	boolean darkenSky;
	boolean dragonMusic;
	boolean thickenFog;
	float percent;
	// Vertical scale of the minibar, where 5 is the default value. This is used to determine the height of the minibar on the screen.
	int scaleSupplier;
	// Package private constructor, hence a description is not needed.
	Minibar(@Nullable Text displayText,
			@NotNull BossBar.Color color,
			@NotNull BossBar.Style style,
			boolean darkenSky,
			boolean dragonMusic,
			boolean thickenFog,
			float percent,
			int scaleSupplier) {
		this.displayText = displayText;
		this.color = color;
		this.style = style;
		this.darkenSky = darkenSky;
		this.dragonMusic = dragonMusic;
		this.thickenFog = thickenFog;
		this.percent = percent;
		this.scaleSupplier = scaleSupplier;
	}

	public void serializeToBuf(PacketByteBuf buf)
	{
		buf.writeText(displayText);
		buf.writeEnumConstant(color);
		buf.writeEnumConstant(style);
		buf.writeBoolean(darkenSky);
		buf.writeBoolean(dragonMusic);
		buf.writeBoolean(thickenFog);
		buf.writeFloat(percent);
		buf.writeInt(scaleSupplier);
	}
	public static Minibar deserializeFromBuf(PacketByteBuf buf)
	{
		Text displayText = buf.readText();
		BossBar.Color color = buf.readEnumConstant(BossBar.Color.class);
		BossBar.Style style = buf.readEnumConstant(BossBar.Style.class);
		boolean darkenSky = buf.readBoolean();
		boolean dragonMusic = buf.readBoolean();
		boolean thickenFog = buf.readBoolean();
		float percent = buf.readFloat();
		int scaleSupplier = buf.readInt();
		return new Minibar(displayText, color, style, darkenSky, dragonMusic, thickenFog, percent, scaleSupplier);
	}

	/**
	 * Sets the vertical scale of the minibar, where 5 is the default value. This is used to determine the height of the minibar on the screen.
	 * @param scaleSupplier A supplier that provides the vertical scale of the minibar. Must not be null.
	 */
	public void setScaleSupplier(int scaleSupplier) {
		this.scaleSupplier = scaleSupplier;
	}

	/**
	 * Sets the display text of the minibar.
	 * @param displayText The display text to set for the minibar, or null if it should be unset.
	 */
	public void setDisplayName(@Nullable Text displayText) {
		this.displayText = displayText;
	}

	/**
	 * Sets the percentage of the minibar, where 1.0f is full and 0.0f is empty.
	 * @param percent The percentage to set for the minibar. Must be between 0.0f and 1.0f inclusive.
	 */
	public void setPercent(float percent) {
		this.percent = Math.clamp(percent,0.0f,1.0f);
	}

	/**
	 * Sets the display text of the minibar.
	 * @param displayText The display text to set for the minibar, or null if it should be unset.
	 */
	public void setDisplayName(@Nullable String displayText) {
		this.displayText = displayText == null ? null : Text.of(displayText);
	}

	/**
	 * Sets the color of the minibar.
	 * @param color The color to set for the minibar. Must not be null.
	 */
	public void setColor(@NotNull BossBar.Color color) {
		this.color = color;
	}

	/**
	 * Sets the style of the minibar.
	 * @param style The style to set for the minibar. Must not be null.
	 */
	public void setStyle(@NotNull BossBar.Style style) {
		this.style = style;
	}

	/**
	 * Sets whether the minibar should darken the sky.
	 * @param darkenSky true if the minibar should darken the sky, false otherwise.
	 */
	public void setDarkenSky(boolean darkenSky) {
		this.darkenSky = darkenSky;
	}

	/**
	 * Sets whether the minibar should play dragon music.
	 * @param dragonMusic true if the minibar should play dragon music, false otherwise.
	 */
	public void setDragonMusic(boolean dragonMusic) {
		this.dragonMusic = dragonMusic;
	}

	/**
	 * Sets whether the minibar should thicken the fog.
	 * @param thickenFog true if the minibar should thicken the fog, false otherwise.
	 */
	public void setThickenFog(boolean thickenFog) {
		this.thickenFog = thickenFog;
	}

	/**
	 * Gets the vertical scale of the minibar, where 5 is the default value. This is used to determine the height of the minibar on the screen.
	 * @return The vertical scale of the minibar.
	 */
	public int getScaleSupplier() {
		return scaleSupplier;
	}

	/**
	 * Gets the percentage of the minibar, where 1.0f is full and 0.0f is empty.
	 * @return The percentage of the minibar.
	 */
	public float getPercent()
	{
		return percent;
	}

	/**
	 * Gets the display text of the minibar.
	 * @return The display text of the minibar, or null if it is not set.
	 */
	public @Nullable Text getDisplayText() {
		return displayText;
	}

	/**
	 * Gets the color of the minibar.
	 * @return The color of the minibar.
	 */
	public @NotNull BossBar.Color getColor() {
		return color;
	}

	/**
	 * Gets the style of the minibar.
	 * @return The style of the minibar.
	 */
	public @NotNull BossBar.Style getStyle() {
		return style;
	}

	/**
	 * Checks if the minibar should darken the sky.
	 * @return true if the minibar should darken the sky, false otherwise.
	 */
	public boolean shouldDarkenSky() {
		return darkenSky;
	}

	/**
	 * Checks if the minibar should play dragon music.
	 * @return true if the minibar should play dragon music, false otherwise.
	 */
	public boolean shouldPlayDragonMusic() {
		return dragonMusic;
	}

	/**
	 * Checks if the minibar should thicken the fog.
	 * @return true if the minibar should thicken the fog, false otherwise.
	 */
	public boolean shouldThickenFog() {
		return thickenFog;
	}

	/**
	 * Creates a minibar from a boss bar.
	 * @param bar The boss bar to create the minibar from.
	 * @return A minibar with the same properties as the given boss bar.
	 */
	public static Minibar of(@NotNull BossBar bar)
	{
		return new Minibar(bar.getName(), bar.getColor(), bar.getStyle(), bar.shouldDarkenSky(), bar.hasDragonMusic(), bar.shouldThickenFog(), bar.getPercent(),5);
	}

	/**
	 * Creates a minibar with the given properties.
	 * @param displayText The display text of the minibar, or null if it is not set.
	 * @param color The color of the minibar.
	 * @param style The style of the minibar.
	 * @param darkenSky Whether the minibar should darken the sky.
	 * @param dragonMusic Whether the minibar should play dragon music.
	 * @param thickenFog Whether the minibar should thicken the fog.
	 * @return A minibar with the given properties.
	 */
	public static Minibar of(@Nullable Text displayText,
							 @NotNull BossBar.Color color,
							 @NotNull BossBar.Style style,
							 boolean darkenSky,
							 boolean dragonMusic,
							 boolean thickenFog,
							 float percent,
							 int scaleSupplier){
		return new Minibar(displayText, color, style, darkenSky, dragonMusic, thickenFog, percent, scaleSupplier);
	}
}
