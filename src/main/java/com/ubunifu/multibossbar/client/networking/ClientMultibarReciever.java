package com.ubunifu.multibossbar.client.networking;

import com.ubunifu.multibossbar.common.barhandler.ClientMultiBossBar;
import com.ubunifu.multibossbar.common.barhandler.Minibar;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientMultibarReciever {
	public static List<ClientMultiBossBar> multiBossBars = new ArrayList<>();

	public static void recieveMultibossBarPacket(
		MultibossBarS2CPacket payload,
		ClientPlayNetworking.Context context)
	{
		PacketByteBuf packetByteBuf = payload.data();
		UUID uuid = packetByteBuf.readUuid();
		ClientMultiBossBar multiBossBar = verifyBarExists(uuid);
		MultibossBarS2CPacket.PacketType packetType = packetByteBuf.readEnumConstant(MultibossBarS2CPacket.PacketType.class);
		switch (packetType)
		{
			case ADD_BAR -> {
				Minibar minibar = Minibar.deserializeFromBuf(packetByteBuf);
				multiBossBar.addBossBar(minibar);
				replaceBar(multiBossBar);
			}
			case REMOVE_BAR -> {
				int index = packetByteBuf.readInt();
				multiBossBar.removeBossBar(index);
				if (multiBossBar.count() == 0)
					multiBossBars.remove(multiBossBar);
				else
					replaceBar(multiBossBar);
			}
			case SET_DISPLAY_NAME -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setDisplayName(index, TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode(packetByteBuf));
				replaceBar(multiBossBar);
			}
			case SET_BAR_COLOR -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setColor(index, packetByteBuf.readEnumConstant(net.minecraft.entity.boss.BossBar.Color.class));
				replaceBar(multiBossBar);
			}
			case SET_BAR_PERCENTAGE -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setPercent(index, packetByteBuf.readFloat());
				replaceBar(multiBossBar);
			}
			case SET_BAR_STYLE -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setStyle(index, packetByteBuf.readEnumConstant(net.minecraft.entity.boss.BossBar.Style.class));
				replaceBar(multiBossBar);
			}
			case SET_DARKEN_SKY -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setDarkenSky(index, packetByteBuf.readBoolean());
				replaceBar(multiBossBar);
			}
			case SET_DRAGON_MUSIC -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setDragonMusic(index, packetByteBuf.readBoolean());
				replaceBar(multiBossBar);
			}
			case SET_THICKEN_FOG -> {
				int index = packetByteBuf.readInt();
				multiBossBar.setThickenFog(index, packetByteBuf.readBoolean());
				replaceBar(multiBossBar);
			}
			case CLEAR_BARS -> {
				multiBossBar.clear();
				if (multiBossBar.count() == 0)
					multiBossBars.remove(multiBossBar);
				else
					replaceBar(multiBossBar);
			}
			case ADD_ALL_BARS -> {
				Text displayName = TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode(packetByteBuf);
				multiBossBar.setText(displayName);
				int barCount = packetByteBuf.readInt();
				for (int i = 0; i < barCount; i++)
				{
					Minibar minibar = Minibar.deserializeFromBuf(packetByteBuf);
					multiBossBar.addBossBar(minibar);
				}
				replaceBar(multiBossBar);
			}
			case SET_NAME -> {
				Text displayName = TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode(packetByteBuf);
				multiBossBar.setText(displayName);
				replaceBar(multiBossBar);
			}
		}
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
