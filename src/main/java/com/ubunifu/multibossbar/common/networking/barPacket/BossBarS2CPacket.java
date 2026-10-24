package com.ubunifu.multibossbar.common.networking.barPacket;

import com.ubunifu.multibossbar.common.MultibossbarMod;
import com.ubunifu.multibossbar.common.boss_bar.*;
import com.ubunifu.multibossbar.common.networking.serverBarPacket.ServerBarPacketType;
import com.ubunifu.multibossbar.server.bossbar.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.payload.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
			case UPDATE_VISIBILITY -> buf.writeBoolean(section.isVisible());
			case UPDATE_TYPE -> buf.writeEnumConstant(section.getType());
			case CREATE -> {
				TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode(buf,section.getName());
				buf.writeDouble(section.getPercentage());
				buf.writeEnumConstant(section.getColor());
				buf.writeEnumConstant(section.getStyle());
				buf.writeEnumConstant(section.getType());
				buf.writeFloat(section.getVerticalScale());
				buf.writeBoolean(section.isVisible());
			}
			default -> throw new UnsupportedOperationException("A case for "+barPacketType.name()+ " has not been implemented yet.");
		}

		BossBarS2CPacket packet = new BossBarS2CPacket(buf);
		players.forEach(packet::send);
	}
	public void receive(ClientPlayNetworking.Context context) {
		PacketByteBuf buf = this.data();
		BarPacketType packetType = buf.readEnumConstant(BarPacketType.class);
		UUID containerUUID = buf.readUuid();
		UUID sectionUUID = buf.readUuid();

		MultibarContainer container = MultibarContainer.getOptionalClientBar(containerUUID).orElse(null);
		if (container == null) {
			MultibossbarMod.LOGGER.warn("Failed to acquire container with uuid: {}",containerUUID);
			return;
		}
		BarSection section = container.getOptionalSection(sectionUUID).orElse(null);

		switch (packetType) {
			case UPDATE_NAME -> {
				Objects.requireNonNull(section);
				section.setName(TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode(buf));
			}
			case UPDATE_PERCENTAGE -> {
				Objects.requireNonNull(section);
				section.setPercentage(buf.readDouble());
			}
			case UPDATE_COLOR -> {
				Objects.requireNonNull(section);
				section.setColor(buf.readEnumConstant(BossBar.Color.class));
			}
			case UPDATE_STYLE -> {
				Objects.requireNonNull(section);
				section.setStyle(buf.readEnumConstant(BossBar.Style.class));
			}
			case UPDATE_SCALE -> {
				Objects.requireNonNull(section);
				section.setVerticalScale(buf.readFloat());
			}
			case REMOVE_BAR -> {
				if (section == null) return;
				container.removeSection(section);
			}
			case UPDATE_VISIBILITY -> {
				Objects.requireNonNull(section);
				section.setVisible(buf.readBoolean());
			}
			case UPDATE_TYPE -> {
				Objects.requireNonNull(section);
				section.setType(buf.readEnumConstant(RowType.class));
			}
			case CREATE -> section = MultibarContainer.createSection(
				container,
				sectionUUID,
				TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.decode(buf),
				buf.readDouble(),
				buf.readEnumConstant(BossBar.Color.class),
				buf.readEnumConstant(BossBar.Style.class),
				buf.readEnumConstant(RowType.class),
				buf.readFloat(),
				buf.readBoolean()
			);
		}
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
		new Id<>(new Identifier(MultibossbarMod.MOD_ID, "section_s2c"));

	public void send(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, this);
	}
}
