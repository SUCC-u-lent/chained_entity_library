package com.ubunifu.chainedentity.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EulerAngle;

/** A common part of all chain parts, literally is just a way to put any chain part in as a return variable */
public interface ChainUnit {
	default EulerAngle getAngle() {
		Entity entity = (Entity) this;
		return new EulerAngle(entity.getPitch(), entity.getYaw(),0);
	}
}
