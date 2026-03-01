package com.ubunifu.multibossbar.client.networking;

import com.ubunifu.multibossbar.common.barhandler.Minibar;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.UUID;

/**
 * Handles client-side networking for MultiBossBar packets.
 */
public class ClientPacketHandler {

	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(
			MultibossBarS2CPacket.ID,
			ClientMultibarReciever::recieveMultibossBarPacket
		);
	}
}
