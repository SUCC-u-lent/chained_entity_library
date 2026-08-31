package com.ubunifu.multibossbar.client.networking;

import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.*;

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
