package com.ubunifu.chainedentity.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

/** A common part of all chain parts, literally is just a way to put any chain part in as a return variable */
public interface ChainUnit {
	ChainType getChainType();
	default EulerAngle getAngle() {
		Entity entity = (Entity) this;
		return new EulerAngle(entity.getPitch(), entity.getYaw(),0);
	}
	default void setAngle(EulerAngle angle) {
		Entity entity = (Entity) this;
		entity.setYaw(angle.getYaw());
		entity.setPitch(angle.getPitch());
	}
	default float getDelay() {
		return this.getChainType().getDelay();
	}
	float getScale();
	ChainUnit[] getChainParts();
	ChainUnit getParent();
	default int getSegmentPosition() {
		int position = 0;
		ChainUnit parent = this;
		while (parent.getParent() != null) {
			position++;
			parent = parent.getParent();
		}
		return position;
	}

	Vec3d getTargetOffset();
	default EulerAngle getTargetAngleOffset() { return new EulerAngle(0,0,0); }
}
