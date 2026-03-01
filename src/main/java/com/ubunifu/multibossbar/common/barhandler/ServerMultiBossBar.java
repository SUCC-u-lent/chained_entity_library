package com.ubunifu.multibossbar.common.barhandler;

import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ServerMultiBossBar extends MultiBossBar
{
	private final List<ServerPlayerEntity> viewers = new ArrayList<>();
	private boolean isVisible;

	public ServerMultiBossBar()
	{ super(); }

	/**
	 * Adds a Minibar to this MultiBossBar.
	 * @param minibar The Minibar instance to be added to this MultiBossBar.
	 */
	@Override
	public void addBossBar(Minibar minibar) {
		super.addBossBar(minibar);
		this.sendPacket(MultibossBarS2CPacket.createAddPacket(minibar));
	}

	private void addAllBars(ServerPlayerEntity player)
	{
		this.sendPacket(player, MultibossBarS2CPacket::createAddAllPacket);
	}
	private void removeAllBars(ServerPlayerEntity player)
	{
		this.sendPacket(player, MultibossBarS2CPacket::createClearPacket);
	}

	public void addPlayer(ServerPlayerEntity player)
	{
		if (viewers.contains(player)) return;
		viewers.add(player);
		this.addAllBars(player);
	}
	public void removePlayer(ServerPlayerEntity player)
	{
		if (!viewers.contains(player)) return;
		viewers.remove(player);
		this.removeAllBars(player);
	}

	@Override
	public void removeBossBar(int index) {
		super.removeBossBar(index);
		this.sendPacket(MultibossBarS2CPacket.createRemoveBarPacket(index));
	}

	@Override
	public void setText(@NotNull Text text) {
		super.setText(text);
		this.sendPacket(MultibossBarS2CPacket.createSetDisplayNamePacket(this,text));
	}

	@Override
	public void setDisplayName(int index, @NotNull Text displayName) {
		super.setDisplayName(index, displayName);
		this.sendPacket(MultibossBarS2CPacket.createSetBarNamePacket(index,displayName));
	}

	@Override
	public void setColor(int index, BossBar.Color color) {
		super.setColor(index, color);
		this.sendPacket(MultibossBarS2CPacket.createSetBarColorPacket(index,color));
	}

	@Override
	public void setPercent(int index, float percent) {
		super.setPercent(index, percent);
		this.sendPacket(MultibossBarS2CPacket.createSetBarPercentagePacket(index,percent));
	}

	@Override
	public void setStyle(int index, BossBar.Style style) {
		super.setStyle(index, style);
		this.sendPacket(MultibossBarS2CPacket.createSetBarStylePacket(index,style));
	}

	@Override
	public void setDarkenSky(int index, boolean darkenSky) {
		super.setDarkenSky(index, darkenSky);
		this.sendPacket(MultibossBarS2CPacket.createSetDarkenSkyBarPacket(index,darkenSky));
	}

	@Override
	public void setDragonMusic(int index, boolean dragonMusic) {
		super.setDragonMusic(index, dragonMusic);
		this.sendPacket(MultibossBarS2CPacket.createSetBarMusicPacket(index,dragonMusic));
	}

	@Override
	public void setThickenFog(int index, boolean thickenFog) {
		super.setThickenFog(index, thickenFog);
		this.sendPacket(MultibossBarS2CPacket.createSetThickenFogBarPacket(index,thickenFog));
	}

	@Override
	public void clear() {
		super.clear();
		this.sendPacket(MultibossBarS2CPacket::createClearPacket);
	}

	/**
	 * Sets whether this MultiBossBar is visible to players.
	 * When set to false, no packets will be sent to clients.
	 * @param visible true to make the boss bar visible, false to hide it
	 */
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	/**
	 * Gets whether this MultiBossBar is visible to players.
	 * @return true if the boss bar is visible, false otherwise
	 */
	public boolean isVisible() {
		return this.isVisible;
	}

	/**
	 * Gets the UUID of this MultiBossBar.
	 * @return The UUID of this MultiBossBar
	 */
	@Override
	public @NotNull UUID getUuid() {
		return this.uuid;
	}

	/**
	 * Gets all players currently viewing this MultiBossBar.
	 * @return A list of all viewers
	 */
	public List<ServerPlayerEntity> getViewers() {
		return new ArrayList<>(viewers);
	}

	private void sendPacket(Function<MultiBossBar,MultibossBarS2CPacket> constructor) {
		if (!isVisible) return;
		for (ServerPlayerEntity viewer : viewers)
			this.sendPacket(viewer, constructor);
	}
	private void sendPacket(ServerPlayerEntity serverPlayer, Function<MultiBossBar,MultibossBarS2CPacket> constructor) {
		if (!isVisible) return;
		MultibossBarS2CPacket packet = constructor.apply(this);
		packet.send(serverPlayer);
	}
}
