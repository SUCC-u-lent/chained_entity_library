package com.ubunifu.multibossbar.common.barhandler;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class MultiBossBar {
	protected List<Minibar> minibars = new ArrayList<>();
	@NotNull protected Text text = Text.empty();
	@NotNull protected UUID uuid;

	public MultiBossBar() {
		this.uuid = UUID.randomUUID();
	}

	/**
	 * Gets the collection of minibars associated with this MultiBossBar.
	 * @return A collection of Minibar instances, representing the minibars associated with this MultiBossBar.
	 * @implNote Modifying the returned collection will not affect the internal state of the boss bar.
	 */
	public Collection<Minibar> getMinibars() {
		return new ArrayList<>(minibars); // Return a copy to prevent external modification
	}

	/**
	 * Adds a Minibar to this MultiBossBar.
	 * @param minibar The Minibar instance to be added to this MultiBossBar.
	 */
	public void addBossBar(Minibar minibar) {
		minibars.add(minibar);
	}
	/**
	 * Adds a BossBar to this MultiBossBar by converting it to a Minibar.
	 * @param bossBar The BossBar instance to be added to this MultiBossBar.
	 */
	public void addBossBar(BossBar bossBar)
	{ this.addBossBar(Minibar.of(bossBar)); }
	public void removeBossBar(int index) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.remove(index);
	}
	public void setText(@NotNull Text text) {
		this.text = text;
	}
	public Minibar get(int index)
	{
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		return minibars.get(index);
	}
	public void setDisplayName(int index, @NotNull Text displayName) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setDisplayName(displayName);
	}
	public void setColor(int index, BossBar.Color color) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setColor(color);
	}
	public void setPercent(int index, float percent) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setPercent(percent);
	}
	public void setStyle(int index, BossBar.Style style) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setStyle(style);
	}
	public void setDarkenSky(int index, boolean darkenSky) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setDarkenSky(darkenSky);
	}
	public void setDragonMusic(int index, boolean dragonMusic) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setDragonMusic(dragonMusic);
	}
	public void setThickenFog(int index, boolean thickenFog) {
		if (isOutOfBounds(index))
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + minibars.size());
		minibars.get(index).setThickenFog(thickenFog);
	}
	public void clear() {
		minibars.clear();
	}

	private boolean isOutOfBounds(int index) {
		return index < 0 || index >= minibars.size();
	}

	public @NotNull UUID getUuid()
	{
		return uuid;
	}

	public Text getDisplayName() {
		return this.text;
	}

	public int count() {
		return this.minibars.size();
	}

	public boolean isDragonMusic() {
		return this.minibars.stream().anyMatch(Minibar::shouldPlayDragonMusic);
	}

	public boolean isDarkenSky() {
		return this.minibars.stream().anyMatch(Minibar::shouldDarkenSky);
	}

	public boolean isThickenFog() {
		return this.minibars.stream().anyMatch(Minibar::shouldThickenFog);
	}
}
