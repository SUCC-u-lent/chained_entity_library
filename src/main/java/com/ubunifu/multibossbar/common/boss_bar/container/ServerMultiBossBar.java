package com.ubunifu.multibossbar.common.boss_bar.container;

import com.google.common.collect.Sets;
import com.ubunifu.multibossbar.common.boss_bar.FillType;
import com.ubunifu.multibossbar.common.boss_bar.sections.ServerBossBarSection;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.function.Function;

public class ServerMultiBossBar extends MultiBossBar {

	private final Set<ServerPlayerEntity> players = Sets.newHashSet();

	public ServerMultiBossBar() {
		super(new ArrayList<>(), FillType.VANILLA);
		this.visible = true;
	}

	public ServerMultiBossBar(List<BossBarSection> bossBarSections, FillType fillType) {
		super(bossBarSections, fillType);
		this.visible = true;
	}

	public static void removeBar(ServerMultiBossBar bar) {
		Set<ServerPlayerEntity> playerEntities = Set.copyOf( bar.players );
		for (ServerPlayerEntity plr : playerEntities) bar.sendPacket(
			MultibossBarS2CPacket.clearBars(bar),
			plr
		);
	}

	public Set<ServerPlayerEntity> getPlayers() { return Set.copyOf( players ); }
	public void setPlayers(Set<ServerPlayerEntity> players) {
		Set<ServerPlayerEntity> previousPlayerList = new HashSet<>(this.players);
		this.players.clear();
		this.players.addAll(players);
		players.forEach(this::sendBars);
		previousPlayerList.removeIf(p->players.stream().anyMatch(r->p.getUuid().equals(r.getUuid())));
		for (ServerPlayerEntity plr : previousPlayerList)
			sendPacket(MultibossBarS2CPacket::clearBars,plr);
	}
	public void resync()
	{
		for (ServerPlayerEntity player : this.players)
			sendPacket(MultibossBarS2CPacket::sendBars,player);
	}
	public void resync(int index)
	{
		BossBarSection section = this.get(index);
		if (section == null) return;
		for (ServerPlayerEntity player : this.players)
			sendPacket(MultibossBarS2CPacket.addBar((ServerBossBarSection) section),player);
	}
	public void removePlayer(ServerPlayerEntity player) {
		if (this.players.removeIf(p->p.getUuid().equals(player.getUuid())))
		{
			sendPacket(MultibossBarS2CPacket::clearBars, player);
		}
	}
	public void addPlayer(ServerPlayerEntity player) {
		if (this.players.stream().anyMatch(p->p.getUuid().equals(player.getUuid()))) return;
		this.players.add(player);
		sendPacket(MultibossBarS2CPacket::sendBars,player);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		sendPacket(p->MultibossBarS2CPacket.setVisible(p,visible));
	}

	public void sendPacket(MultibossBarS2CPacket multibossBarS2CPacket, ServerPlayerEntity player) {
		multibossBarS2CPacket.send( player );
	}

	private void sendPacket(Function<ServerMultiBossBar, MultibossBarS2CPacket> bossBarToPacketFunction, ServerPlayerEntity player) {
		MultibossBarS2CPacket packet = bossBarToPacketFunction.apply( this );
		packet.send( player );
	}
	private void sendPacket(Function<ServerMultiBossBar, MultibossBarS2CPacket> bossBarToPacketFunction) {
		Set<ServerPlayerEntity> serverPlayerEntities = this.getPlayers();
		serverPlayerEntities.forEach(s->sendPacket(bossBarToPacketFunction, s));
	}

	public void sendBars(ServerPlayerEntity player){
		MultibossBarS2CPacket packet = MultibossBarS2CPacket.sendBars(this);
		packet.send(player);
	}

	public void clearBars(ServerPlayerEntity player){
		MultibossBarS2CPacket packet = MultibossBarS2CPacket.clearBars(this);
		packet.send(player);
	}
}
