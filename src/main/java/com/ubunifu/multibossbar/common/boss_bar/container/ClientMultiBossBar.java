package com.ubunifu.multibossbar.common.boss_bar.container;

import com.google.common.collect.Sets;
import com.ubunifu.multibossbar.common.boss_bar.FillType;
import com.ubunifu.multibossbar.common.boss_bar.sections.framework.BossBarSection;
import com.ubunifu.multibossbar.common.networking.MultibossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.function.Function;

public class ClientMultiBossBar extends MultiBossBar {
	public ClientMultiBossBar(UUID uuid, List<BossBarSection> bossBarSections, FillType fillType)
	{
		super(uuid, bossBarSections, fillType);
	}
	public ClientMultiBossBar(List<BossBarSection> bossBarSections, FillType fillType) {
		super(bossBarSections, fillType);
	}

	public ClientMultiBossBar() {
		super(new ArrayList<>(), FillType.VANILLA);
	}
}
