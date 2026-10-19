package com.ubunifu.multibossbar.common.boss_bar.container;

import com.google.common.collect.Sets;
import com.ubunifu.multibossbar.common.boss_bar.*;
import com.ubunifu.multibossbar.common.boss_bar.sections.ServerBossBarSection;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.NotImplementedException;

import java.io.NotSerializableException;
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
		for (ServerPlayerEntity plr : playerEntities)
			bar.clearBars(plr);
	}

	public Set<ServerPlayerEntity> getPlayers() { return Set.copyOf( players ); }
	public void setPlayers(Set<ServerPlayerEntity> players) {
		Set<ServerPlayerEntity> previousPlayerList = new HashSet<>(this.players);
		this.players.clear();
		this.players.addAll(players);
		players.forEach(this::sendBars);
		previousPlayerList.removeIf(p->players.stream().anyMatch(r->p.getUuid().equals(r.getUuid())));
		for (ServerPlayerEntity plr : previousPlayerList)
			clearBars(plr);
	}
	public void resync()
	{
		for (ServerPlayerEntity player : this.players)
			sendBars(player);
	}
	public void removePlayer(ServerPlayerEntity player) {
		if (this.players.removeIf(p->p.getUuid().equals(player.getUuid())))
			clearBars(player);
	}
	public void addPlayer(ServerPlayerEntity player) {
		if (this.players.stream().anyMatch(p->p.getUuid().equals(player.getUuid()))) return;
		this.players.add(player);
		sendBars(player);
	}

	@Override
	public void setDisplayName(Text text) {
		super.setDisplayName(text);
		onUpdate(PacketType.UPDATE_CONTAINER_NAME,null);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		onUpdate(PacketType.UPDATE_VISIBILITY,null, visible);
	}

	public void sendBars(ServerPlayerEntity player){
		onUpdate(PacketType.SEND_ALL_BARS, player);
	}

	public void clearBars(ServerPlayerEntity player){
		onUpdate(PacketType.CLEAR_BARS, player);
	}

	public void onBarUpdate(PacketType type, ServerBossBarSection section) {
		if (!type.isBarUpdate()) throw new IllegalStateException("Attempt to update bar using type: "+type.name()+" via 'onBarUpdate'. Use 'onUpdate' for this PacketType");
		MultibossBarS2CPacket packet = switch (type) {
			case REMOVE_BAR -> MultibossBarS2CPacket.removeBar(section);
			case ADD_BAR -> MultibossBarS2CPacket.addBar(section);
			case UPDATE_NAME -> MultibossBarS2CPacket.updateName(section);
			case UPDATE_STYLE -> MultibossBarS2CPacket.updateStyle(section);
			case UPDATE_PERCENT -> MultibossBarS2CPacket.updateProgress(section);
			case UPDATE_COLOR -> MultibossBarS2CPacket.updateColor(section);
			case UPDATE_DARKEN_SKY -> MultibossBarS2CPacket.updateDarkenSky(section);
			case UPDATE_DRAGON_MUSIC -> MultibossBarS2CPacket.updateDragonMusic(section);
			case UPDATE_THICKEN_FOG -> MultibossBarS2CPacket.updateThickenFog(section);
			case SET_BAR_SCALE -> MultibossBarS2CPacket.setScale(section);
			case UPDATE_SECTION_TYPE -> MultibossBarS2CPacket.updateSectionType(section);
			default -> throw new NotImplementedException();
		};
		this.players.forEach(packet::send);
	}
	public void onUpdate(PacketType type, ServerPlayerEntity player, Object... args)
	{
		if (type.isBarUpdate()) throw new IllegalStateException("Attempt to update bar using type: "+type.name()+" via 'onUpdate'. Use 'onBarUpdate' for this PacketType");
		MultibossBarS2CPacket packet = switch (type) {
			case CREATE_BAR -> MultibossBarS2CPacket.createBar(this);
			case CLEAR_BARS -> MultibossBarS2CPacket.clearBars(this);
			case SEND_ALL_BARS -> MultibossBarS2CPacket.sendBars(this);
			case UPDATE_VISIBILITY -> MultibossBarS2CPacket.setVisible(this, (boolean)args[0]);
			case RESYNC -> MultibossBarS2CPacket.resync(this);
			case UPDATE_CONTAINER_NAME -> MultibossBarS2CPacket.updateContainerName(this);
			default -> throw new NotImplementedException();
		};
		if (player != null) packet.send(player);
		else this.players.forEach(packet::send);
	}
}
