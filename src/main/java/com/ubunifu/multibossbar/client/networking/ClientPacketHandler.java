package com.ubunifu.multibossbar.client.networking;

import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Handles client-side networking for MultiBossBar packets.
 */
public class ClientPacketHandler {

	public static void registerPackets() {
		PayloadTypeRegistry.playS2C().register( MultibossBarS2CPacket.ID, MultibossBarS2CPacket.CODEC );
		ClientPlayNetworking.registerGlobalReceiver(
			MultibossBarS2CPacket.ID,
			ClientMultibarReciever::recieveMultibossBarPacket
		);
	}
}
