package com.ubunifu.multibossbar.common.networking.barPacket;

import com.ubunifu.multibossbar.common.MultibossbarMod;
import com.ubunifu.multibossbar.common.boss_bar.*;
import com.ubunifu.multibossbar.server.bossbar.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.payload.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BossBarS2CPacket(@NotNull PacketByteBuf data) implements CustomPayload {

	public static final PacketCodec<? super PacketByteBuf, BossBarS2CPacket> CODEC =
		new PacketCodec<>() {
			@Override
			public void encode(PacketByteBuf buf, BossBarS2CPacket value) {
				buf.writeBytes(value.data());
			}

			@Override
			public BossBarS2CPacket decode(PacketByteBuf buf) {
				return new BossBarS2CPacket(
					new PacketByteBuf(buf.readBytes(buf.readableBytes()))
				);
			}
		};

	public static void sendPacket(BarPacketType barPacketType,
	                              MultibarContainer container,
	                              BarSection section,
								  List<ServerPlayerEntity> players) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeEnumConstant(barPacketType);
		buf.writeUuid(container.getUuid());
		buf.writeUuid(section.getUuid());

		switch (barPacketType) {
			case UPDATE_NAME -> TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode(buf, section.getName());
			case UPDATE_PERCENTAGE -> buf.writeDouble(section.getPercentage());
			case UPDATE_COLOR -> buf.writeEnumConstant(section.getColor());
			case UPDATE_STYLE -> buf.writeEnumConstant(section.getStyle());
			case UPDATE_SCALE -> buf.writeFloat(section.getVerticalScale());
			case REMOVE_BAR -> {} // Got all shit.
			case CREATE,ADD_BAR -> {
				TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode(buf,section.getName());
				buf.writeDouble(section.getPercentage());
				buf.writeEnumConstant(section.getColor());
				buf.writeEnumConstant(section.getStyle());
				buf.writeFloat(section.getVerticalScale());
			}
			default -> throw new UnsupportedOperationException("A case for "+barPacketType.name()+ " has not been implemented yet.");
		}

		BossBarS2CPacket packet = new BossBarS2CPacket(buf);
		players.forEach(packet::send);
	}
	public static void sendPacket(BarPacketType barPacketType,
	                              MultibarContainer container,
	                              BarSection section) {
		if (container instanceof ServerMultibarContainer multibarContainer)
			sendPacket(barPacketType,container,section,multibarContainer.getPlayers());
	}


	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static final Id<BossBarS2CPacket> ID =
		new Id<>(new Identifier(MultibossbarMod.MOD_ID, "multibossbar_s2c"));

	public void send(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, this);
	}

	public void recieve(ClientPlayNetworking.Context context) {
	}
}
