package com.ubunifu.chainedentity.common;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.entity.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;

import java.util.*;

final class ChainOperator {
	private static final Map<UUID,ChainUnit> CHAIN_UNITS = new HashMap<>();
	static void Register() {
		ServerEntityEvents.ENTITY_LOAD.register((entity,world)->{
			if (!(entity instanceof ChainUnit cu)) return;
			CHAIN_UNITS.put(entity.getUuid(), cu);
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity,world)-> CHAIN_UNITS.remove(entity.getUuid()));
		ServerTickEvents.END_WORLD_TICK.register((world)->{
			MinecraftServer server = world.getServer();
			CHAIN_UNITS.forEach((uuid,unit)->{
				if (!(unit instanceof Entity ue)) return;
				if (!ue.getWorld().equals(world)) return;
				switch (unit.getChainType()) {
					case DELAYED_FOLLOW -> operateDelayedFollow(world,server,ue);
					case SERPENTINE -> operateSerpentine(world,server,ue);
				}
			});
		});
	}

	private static EulerAngle addEulerAngle(EulerAngle a, EulerAngle b) {
		return new EulerAngle(
			a.getPitch() + b.getPitch(),
			a.getYaw() + b.getYaw(),
			a.getRoll() + b.getRoll()
		);
	}

	private static void operateDelayedFollow(
		ServerWorld world,
		MinecraftServer server,
		Entity entity
	) {
		ChainUnit unit = (ChainUnit) entity;
		if (!(unit.getParent() instanceof Entity parent)) return;

		Vec3d parentPos = parent.getPos().add(unit.getTargetOffset());

		Vec3d offset = parentPos.subtract(entity.getPos());
		EulerAngle offsetAngle = unit.getAngle();

		double stiffness = unit.getChainType().getDelay();
		double damping = 0.15;

		Vec3d acceleration = offset.multiply(stiffness)
			.subtract(entity.getVelocity().multiply(damping));

		entity.addVelocity(
			acceleration.x,
			acceleration.y,
			acceleration.z
		);

		// Rotation
		EulerAngle current = unit.getAngle();
		EulerAngle target = addEulerAngle(((ChainUnit) parent).getAngle(), unit.getTargetAngleOffset());

		float rotationFactor = 0.15f;

		unit.setAngle(new EulerAngle(
			MathHelper.lerp(
				rotationFactor, current.getPitch(), target.getPitch()
			),
			MathHelper.lerpDegrees(
				rotationFactor, current.getYaw(), target.getYaw()
			),
			MathHelper.lerpDegrees(
				rotationFactor, current.getRoll(), target.getRoll()
			)
		));
	}

	private record EulerHistory(Long age, EulerAngle angle) {}
	private static final Map<UUID, Deque<EulerHistory>> HISTORY_EULER_MAP = new HashMap<>();
	private static EulerAngle getAngleAtTick(Deque<EulerHistory> history, long targetTick) {
		if (history == null || history.isEmpty()) {
			return null;
		}

		for (EulerHistory entry : history) {
			if (entry.age() == targetTick) {
				return entry.angle();
			}
		}

		return null; // That tick isn't in the history
	}
	private static void operateSerpentine(ServerWorld world, MinecraftServer server, Entity entity) {
		ChainUnit chainUnit = (ChainUnit) entity;
		Deque<EulerHistory> history = HISTORY_EULER_MAP.computeIfAbsent(
			entity.getUuid(),
			uuid -> new ArrayDeque<>()
		);

		history.addLast(new EulerHistory(
			world.getTime(),
			chainUnit.getAngle()
		));
		ChainUnit parentUnit = chainUnit.getParent();
		Entity parent = ((Entity) chainUnit.getParent());
		if (!(parent instanceof LivingEntity parentLivingEntity) || parentLivingEntity.isDead() || !parentLivingEntity.isPartOfGame()) return;
		if (parentLivingEntity.isRemoved()) {
			if (!entity.getWorld().isClient) entity.discard();
			return;
		}
		final long delay = (long) (chainUnit.getSegmentPosition() * chainUnit.getChainType().getDelay());
		final long targetTick = world.getTime() - delay;
		Deque<EulerHistory> partHistory = HISTORY_EULER_MAP.getOrDefault(parent.getUuid(), new ArrayDeque<>());

		final double spacing = 1;
		final double maxBend = Math.toRadians(30); // how far it may curl away from straight behind

		EulerAngle parentAngle = getAngleAtTick(partHistory, targetTick);
		if (parentAngle == null) parentAngle = parentUnit.getAngle();
		Vec3d parentPos = parentLivingEntity.getPos();

		float p = (float) Math.toRadians(parentAngle.getPitch());
		float y = (float) Math.toRadians(parentAngle.getYaw());
		Vec3d behind = new Vec3d(
			MathHelper.sin(y) * MathHelper.cos(p),
			MathHelper.sin(p),
			-MathHelper.cos(y) * MathHelper.cos(p)
		);

		Vec3d dir = entity.getPos().subtract(parentPos);
		dir = dir.lengthSquared() < 1.0E-6 ? behind : dir.normalize();

		double dot = MathHelper.clamp(dir.dotProduct(behind), -1.0, 1.0);
		double angle = Math.acos(dot);
		if (angle > maxBend) {
			Vec3d perp = dir.subtract(behind.multiply(dot));
			if (perp.lengthSquared() < 1.0E-6) {
				// exactly opposite: pick any sideways direction
				perp = behind.crossProduct(new Vec3d(0, 1, 0));
				if (perp.lengthSquared() < 1.0E-6) perp = behind.crossProduct(new Vec3d(1, 0, 0));
			}
			perp = perp.normalize();
			dir = behind.multiply(Math.cos(maxBend)).add(perp.multiply(Math.sin(maxBend)));
		}

		Vec3d targetPos = parentPos.add(dir.multiply(spacing * chainUnit.getScale()));

		Vec3d look = parentPos.subtract(targetPos);
		double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
		float newYaw = (float) Math.toDegrees(Math.atan2(-look.x, look.z));
		float newPitch = (float) -Math.toDegrees(Math.atan2(look.y, horizontal));
		float parentRoll = parentAngle.getRoll();
		float myRoll = chainUnit.getAngle().getRoll();
		float newRoll = MathHelper.lerpDegrees(0.1F, myRoll, parentRoll); // lower = longer trailing wave

		entity.refreshPositionAndAngles(targetPos.x, targetPos.y, targetPos.z, newYaw, newPitch);
		chainUnit.setAngle(new EulerAngle(newPitch, newYaw, newRoll));
	}
}
