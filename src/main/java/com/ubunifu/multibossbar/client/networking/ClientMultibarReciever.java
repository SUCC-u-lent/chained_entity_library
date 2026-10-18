package com.ubunifu.multibossbar.client.networking;

import com.ubunifu.multibossbar.common.boss_bar.PacketType;
import com.ubunifu.multibossbar.common.boss_bar.container.*;
import com.ubunifu.multibossbar.common.boss_bar.sections.ClientBossBarSection;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClientMultibarReciever {
	public static List<ClientMultiBossBar> multiBossBars = new ArrayList<>();

	public static void recieveMultibossBarPacket(
		MultibossBarS2CPacket payload,
		ClientPlayNetworking.Context context)
	{
		PacketByteBuf packetByteBuf
			= payload.data();

		UUID bossBarUUID = packetByteBuf.readUuid();
		boolean isSection = packetByteBuf.readBoolean();
		if (isSection) receiveSectionBar(bossBarUUID, packetByteBuf);
		else receiveContainerBar(bossBarUUID, packetByteBuf);
	}

	private static void receiveContainerBar(UUID bossBarUUID, PacketByteBuf packetByteBuf) {
		ClientMultiBossBar bossBar = verifyBarExists(bossBarUUID);
		processBody(bossBar,null,packetByteBuf);
	}

	private static void receiveSectionBar(UUID bossBarUUID, PacketByteBuf packetByteBuf) {
		int barIndex = packetByteBuf.readInt();
		ClientMultiBossBar bossBar = verifyBarExists(bossBarUUID);
		ClientBossBarSection section = (ClientBossBarSection) bossBar.getBossBars().get( barIndex );
		processBody(bossBar,section,packetByteBuf);
	}
	private static void processBody(ClientMultiBossBar bossBar, @Nullable ClientBossBarSection section, PacketByteBuf packetByteBuf) {
		// Upon entering this the packet buffer has already consumed all the "headers" meaning all thats left is the true "payload" part.
		PacketType packetType = packetByteBuf.readEnumConstant(PacketType.class); // Always first
		// Everything after this changes based on the above packet type.
		switch (packetType){
			case UPDATE_NAME -> {
				Text text = TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode( packetByteBuf );
				Objects.requireNonNull(section);
				section.setName( text );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case UPDATE_STYLE -> {
				BossBar.Style style = packetByteBuf.readEnumConstant( BossBar.Style.class );
				Objects.requireNonNull(section);
				section.setStyle( style );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case UPDATE_PROGRESS -> {
				float progress = packetByteBuf.readFloat();
				Objects.requireNonNull(section);
				section.setPercent( progress );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case UPDATE_COLOR -> {
				BossBar.Color style = packetByteBuf.readEnumConstant( BossBar.Color.class );
				Objects.requireNonNull(section);
				section.setColor( style );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case UPDATE_DARKEN_SKY -> {
				boolean darkenSky = packetByteBuf.readBoolean();
				Objects.requireNonNull(section);
				section.setDarkenSky( darkenSky );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case UPDATE_DRAGON_MUSIC -> {
				boolean darkenSky = packetByteBuf.readBoolean();
				Objects.requireNonNull(section);
				section.setDragonMusic( darkenSky );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case UPDATE_THICKEN_FOG -> {
				boolean darkenSky = packetByteBuf.readBoolean();
				Objects.requireNonNull(section);
				section.setThickenFog( darkenSky );
				bossBar.replaceBar(section);
				replaceBar(bossBar);
			}
			case REMOVE_BAR -> {
				Objects.requireNonNull(section);
				bossBar.removeBossBar( section.getBarIndex() );
				replaceBar(bossBar);
			}
			case CLEAR_BARS -> removeBar(bossBar);
			case ADD_BAR -> {
				int index = packetByteBuf.readInt();
				BossBarSection section1 = BossBarSection.deserializeFromBuf(packetByteBuf);
				bossBar.setBossBar(index,section1);
				replaceBar(bossBar);
			}
			case SEND_ALL_BARS -> {
				bossBar.clearBars();
				int barCount = packetByteBuf.readInt();
				for (int i = 0; i < barCount; i++) {
					BossBarSection section1 = BossBarSection.deserializeFromBuf(packetByteBuf);
					bossBar.addBossBar(section1);
				}
				replaceBar(bossBar);
			}
			case UPDATE_VISIBILITY -> {
				boolean visibility = packetByteBuf.readBoolean();
				bossBar.setVisible(visibility);
				replaceBar(bossBar);
			}
		}
	}

	private static void removeBar(ClientMultiBossBar bossBar) {
		multiBossBars.removeIf(bar -> bar.getUuid().equals(bossBar.getUuid()));
	}

	private static ClientMultiBossBar verifyBarExists(UUID uuid)
	{
		ClientMultiBossBar multiBossBar = multiBossBars.stream().filter(bar -> bar.getUuid().equals(uuid)).findFirst().orElse(null);
		if (multiBossBar == null)
		{
			multiBossBar = new ClientMultiBossBar();
			multiBossBars.add(multiBossBar);
		}
		return multiBossBar;
	}
	private static void replaceBar(ClientMultiBossBar newBar)
	{
		multiBossBars.removeIf(bar -> bar.getUuid().equals(newBar.getUuid()));
		multiBossBars.add(newBar);
	}
}
