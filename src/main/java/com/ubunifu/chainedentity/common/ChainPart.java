package com.ubunifu.chainedentity.common;

public interface ChainPart extends ChainUnit {
	<T extends ChainUnit> T getParent();
	/** Defines how long this part is, the front of the next segment will be placed this far FROM THE CENTER of the parent entity. */
	float getLength();
}
