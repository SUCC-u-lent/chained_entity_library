package com.ubunifu.multibossbar.client;

import com.ubunifu.multibossbar.client.networking.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("deprecation")
public class ClientMultibossbarMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPacketHandler.registerPackets();
	}
}
