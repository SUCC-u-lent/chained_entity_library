package com.ubunifu.multibossbar.client;

import com.ubunifu.multibossbar.client.networking.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import org.quiltmc.loader.api.ModContainer;

@SuppressWarnings("deprecation")
public class ClientMultibossbarMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPacketHandler.registerS2CPackets();
	}
}
