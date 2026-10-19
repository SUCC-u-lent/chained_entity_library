package com.ubunifu.multibossbar.common.networking;

import com.ubunifu.multibossbar.common.MultibossbarMod;
import com.ubunifu.multibossbar.common.boss_bar.PacketType;
import com.ubunifu.multibossbar.common.boss_bar.container.*;
import com.ubunifu.multibossbar.common.boss_bar.sections.ServerBossBarSection;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.payload.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.*;

public record MultibossBarS2CPacket(@NotNull PacketByteBuf data) implements CustomPayload {

	public static final PacketCodec<? super PacketByteBuf, MultibossBarS2CPacket> CODEC =
		new PacketCodec<>() {
			@Override
			public void encode(PacketByteBuf buf, MultibossBarS2CPacket value) {
				buf.writeBytes(value.data());
			}

			@Override
			public MultibossBarS2CPacket decode(PacketByteBuf buf) {
				return new MultibossBarS2CPacket(
					new PacketByteBuf(buf.readBytes(buf.readableBytes()))
				);
			}
		};


	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static final Id<MultibossBarS2CPacket> ID =
		new Id<>(new Identifier(MultibossbarMod.MOD_ID, "multibossbar_s2c"));

	public void send(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, this);
	}

	public static MultibossBarS2CPacket updateProgress(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder(serverBossBarSection);
		builder.setPacketType( PacketType.UPDATE_PERCENT);
		builder.setRemainingConsumer((p)->p.writeFloat(serverBossBarSection.percent));
		return builder.build();
	}
	public static MultibossBarS2CPacket sendBars(ServerMultiBossBar serverMultiBossBar) {
		Builder builder = new Builder( serverMultiBossBar );
		builder.setPacketType( PacketType.SEND_ALL_BARS );
		builder.setRemainingConsumer(serverMultiBossBar::writeToBuffer);
		return builder.build();
	}

	public static MultibossBarS2CPacket updateContainerName(ServerMultiBossBar serverMultiBossBar) {
		Builder builder = new Builder( serverMultiBossBar );
		builder.setPacketType( PacketType.UPDATE_CONTAINER_NAME );
		builder.setRemainingConsumer( p->TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode(p,serverMultiBossBar.getDisplayName()) );
		return builder.build();
	}
	public static MultibossBarS2CPacket resync(ServerMultiBossBar serverMultiBossBar) {
		Builder builder = new Builder( serverMultiBossBar );
		builder.setPacketType( PacketType.RESYNC );
		builder.setRemainingConsumer(serverMultiBossBar::writeToBuffer);
		return builder.build();
	}

	public static MultibossBarS2CPacket clearBars(ServerMultiBossBar serverMultiBossBar) {
		Builder builder = new Builder( serverMultiBossBar );
		builder.setPacketType( PacketType.CLEAR_BARS );
		return builder.build();
	}

	public static MultibossBarS2CPacket updateColor(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.UPDATE_COLOR );
		builder.setRemainingConsumer((p)->{
			p.writeEnumConstant( serverBossBarSection.color );
		});
		return builder.build();
	}

	public static MultibossBarS2CPacket updateStyle(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.UPDATE_STYLE );
		builder.setRemainingConsumer((p)-> p.writeEnumConstant( serverBossBarSection.style ));
		return builder.build();
	}

	public static MultibossBarS2CPacket updateDarkenSky(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.UPDATE_DARKEN_SKY );
		builder.setRemainingConsumer(p->p.writeBoolean(serverBossBarSection.darkenSky));
		return builder.build();
	}

	public static MultibossBarS2CPacket setScale(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.SET_BAR_SCALE );
		builder.setRemainingConsumer(p->p.writeInt(serverBossBarSection.getScaleSupplier()));
		return builder.build();
	}

	public static MultibossBarS2CPacket updateDragonMusic(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.UPDATE_DRAGON_MUSIC );
		builder.setRemainingConsumer(p->p.writeBoolean(serverBossBarSection.dragonMusic));
		return builder.build();
	}

	public static MultibossBarS2CPacket updateThickenFog(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.UPDATE_THICKEN_FOG );
		builder.setRemainingConsumer(p->p.writeBoolean(serverBossBarSection.thickenFog));
		return builder.build();
	}

	public static MultibossBarS2CPacket updateName(ServerBossBarSection serverBossBarSection) {
		Builder builder = new Builder( serverBossBarSection );
		builder.setPacketType( PacketType.UPDATE_NAME );
		builder.setRemainingConsumer(p->TextCodecs.UNLIMITED_TEXT_PACKET_CODEC.encode( p, serverBossBarSection.name ));
		return builder.build();
	}
	public static MultibossBarS2CPacket createBar(ServerMultiBossBar serverBossBar) {
		Builder builder = new Builder( serverBossBar );
		builder.setPacketType( PacketType.CREATE_BAR );
		return builder.build();
	}

	public static MultibossBarS2CPacket removeBar(ServerBossBarSection serverMultiBossBar) {
		Builder builder = new Builder(serverMultiBossBar);
		builder.setPacketType(PacketType.REMOVE_BAR);
		return builder.build();
	}

	public static MultibossBarS2CPacket updateSectionType(ServerBossBarSection section) {
		Builder builder = new Builder(section);
		builder.setPacketType(PacketType.UPDATE_SECTION_TYPE);
		builder.setRemainingConsumer(p->p.writeEnumConstant(section.getSectionType()));
		return builder.build();
	}

	public static MultibossBarS2CPacket setVisible(ServerMultiBossBar serverMultiBossBar, boolean visible) {
		Builder builder = new Builder(serverMultiBossBar);
		builder.setPacketType(PacketType.UPDATE_VISIBILITY);
		builder.setRemainingConsumer(p->p.writeBoolean(visible));
		return builder.build();
	}

	public static MultibossBarS2CPacket addBar(ServerBossBarSection serverMultiBossBar) {
		Builder builder = new Builder( serverMultiBossBar.getContainer() );
		builder.setPacketType( PacketType.ADD_BAR );
		builder.setRemainingConsumer((p)->{
			p.writeInt(serverMultiBossBar.getBarIndex());
			serverMultiBossBar.serializeToBuf( p );
		});
		return builder.build();
	}

	public static class Builder {
		private PacketByteBuf buffer;
		private PacketType packetType;
		private Consumer<PacketByteBuf> consumer;

		public Builder(BossBarSection section) {
			this.buffer = PacketByteBufs.create();
			UUID uuid = section.getContainer().getUuid();
			int index = section.getBarIndex();
			this.buffer.writeUuid( uuid );
			this.buffer.writeBoolean( true );
			this.buffer.writeInt( index );
		}
		public Builder(MultiBossBar bossBar) {
			this.buffer = PacketByteBufs.create();
			UUID uuid = bossBar.getUuid();
			this.buffer.writeUuid( uuid );
			this.buffer.writeBoolean( false ); // Indicates if its a bar section.
		}
		public Builder setPacketType(PacketType packetType) { this.packetType = packetType; return this; }
		public Builder setRemainingConsumer(Consumer<PacketByteBuf> consumer) {
			this.consumer = consumer;
			return this;
		}
		public MultibossBarS2CPacket build() {
			this.buffer.writeEnumConstant( this.packetType );
			if (this.consumer != null) this.consumer.accept( this.buffer );
			return new MultibossBarS2CPacket( this.buffer );
		}
	}
}
