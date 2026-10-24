package com.ubunifu.multibossbar.client.networking;

import com.ubunifu.multibossbar.common.networking.barPacket.BossBarS2CPacket;
import com.ubunifu.multibossbar.common.networking.serverBarPacket.BossBarContainerS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Handles client-side networking for MultiBossBar packets.
 */
public class ClientPacketHandler {

	public static void registerPackets() {
		PayloadTypeRegistry.playS2C().register( BossBarContainerS2CPacket.ID, BossBarContainerS2CPacket.CODEC );
		PayloadTypeRegistry.playS2C().register( BossBarS2CPacket.ID, BossBarS2CPacket.CODEC );
		ClientPlayNetworking.registerGlobalReceiver(BossBarContainerS2CPacket.ID, BossBarContainerS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(BossBarS2CPacket.ID, BossBarS2CPacket::receive);
	}
}
