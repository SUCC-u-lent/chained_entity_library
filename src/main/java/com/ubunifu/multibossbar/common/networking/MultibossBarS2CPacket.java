package com.ubunifu.multibossbar.common.networking;

import com.ubunifu.multibossbar.common.MultibossbarMod;
import com.ubunifu.multibossbar.common.barhandler.Minibar;
import com.ubunifu.multibossbar.common.barhandler.MultiBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.function.Function;

public class MultibossBarS2CPacket {
	public enum PacketType {
		ADD_BAR,
		REMOVE_BAR,
		SET_DISPLAY_NAME,
		SET_BAR_COLOR,
		SET_BAR_PERCENTAGE,
		SET_BAR_STYLE,
		SET_DARKEN_SKY,
		SET_DRAGON_MUSIC,
		SET_THICKEN_FOG,
		CLEAR_BARS,
		ADD_ALL_BARS,
		SET_NAME
	}
	public static final Identifier ID = new Identifier(MultibossbarMod.MOD_ID,"multibossbar_s2c");

	@NotNull final PacketByteBuf data;
	public MultibossBarS2CPacket(@NotNull PacketByteBuf data)
	{this.data = data;}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createAddPacket(Minibar minibar) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.ADD_BAR);
			minibar.serializeToBuf(buf);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createRemoveBarPacket(int index) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.REMOVE_BAR);
			buf.writeInt(index);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetDisplayNamePacket(MultiBossBar serverMultiBossBar, @NotNull Text text)
	{
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(serverMultiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_NAME);
			buf.writeText(text);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetBarNamePacket(int index, @NotNull Text displayName) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_DISPLAY_NAME);
			buf.writeInt(index);
			buf.writeText(displayName);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetBarColorPacket(int index, BossBar.Color color) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_BAR_COLOR);
			buf.writeInt(index);
			buf.writeEnumConstant(color);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetBarPercentagePacket(int index, float percent) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_BAR_PERCENTAGE);
			buf.writeInt(index);
			buf.writeFloat(percent);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetBarStylePacket(int index, BossBar.Style style) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_BAR_STYLE);
			buf.writeInt(index);
			buf.writeEnumConstant(style);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetDarkenSkyBarPacket(int index, boolean darkenSky) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_DARKEN_SKY);
			buf.writeInt(index);
			buf.writeBoolean(darkenSky);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetBarMusicPacket(int index, boolean dragonMusic) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_DRAGON_MUSIC);
			buf.writeInt(index);
			buf.writeBoolean(dragonMusic);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static Function<MultiBossBar, MultibossBarS2CPacket> createSetThickenFogBarPacket(int index, boolean thickenFog) {
		return (multiBossBar) -> {
			PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
			buf.writeUuid(multiBossBar.getUuid());
			buf.writeEnumConstant(PacketType.SET_THICKEN_FOG);
			buf.writeInt(index);
			buf.writeBoolean(thickenFog);
			return new MultibossBarS2CPacket(buf);
		};
	}

	public static MultibossBarS2CPacket createAddAllPacket(MultiBossBar multiBossBar) {
		PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
		buf.writeUuid(multiBossBar.getUuid());
		buf.writeEnumConstant(PacketType.ADD_ALL_BARS);
		buf.writeText(multiBossBar.getDisplayName());
		buf.writeInt(multiBossBar.getMinibars().size());
		for (Minibar minibar : multiBossBar.getMinibars())
			minibar.serializeToBuf(buf);
		return new MultibossBarS2CPacket(buf);
	}

	public static MultibossBarS2CPacket createClearPacket(MultiBossBar multiBossBar)
	{
		PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
		buf.writeUuid(multiBossBar.getUuid());
		buf.writeEnumConstant(PacketType.CLEAR_BARS);
		return new MultibossBarS2CPacket(buf);
	}

	public void send(ServerPlayerEntity player)
	{ ServerPlayNetworking.send(player, ID, data); }
}
