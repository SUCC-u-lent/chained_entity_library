package com.ubunifu.chainedentity.common;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

final class InverseKinematicsOperator {
	private static final Map<UUID,ChainRoot> CHAIN_UNITS = new HashMap<>();
	static void Register() {
		ServerEntityEvents.ENTITY_LOAD.register((entity,world)->{
			if (!(entity instanceof ChainRoot cu)) return;
			CHAIN_UNITS.put(entity.getUuid(), cu);
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity,world)-> CHAIN_UNITS.remove(entity.getUuid()));
		ServerTickEvents.END_WORLD_TICK.register((world)->{
			MinecraftServer server = world.getServer();
			CHAIN_UNITS.forEach((uuid,unit)->{
				if (!(unit instanceof Entity ue)) return;
				operate(world,server,ue);
			});
		});
	}

	private static void operate(ServerWorld world, MinecraftServer server, Entity entity) {
		Object[] chainData = ((ChainRoot)entity).getChainData();
		Vec3d rootPos = entity.getPos();
		ChainUnit[] chainParts = ((ChainRoot)entity).getParts();
		ChainEnd chainEnd = Arrays.stream(chainParts).filter(c->c instanceof ChainEnd).map(c->(ChainEnd)c).findFirst().orElse(null);
		if (chainEnd == null) {
			ChainedEntityMod.LOGGER.warn("Unable to form IK Rig. Missing ChainEnd part.");
			return;
		}
		Vec3d endPos = ((Entity)chainEnd).getPos();

	}
}
