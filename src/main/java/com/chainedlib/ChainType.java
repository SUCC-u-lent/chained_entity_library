package com.chainedlib;

/** The type of the chain, defined in the {@link ChainUnit} */
public enum ChainType {
	/** Makes this part lag behind its parent. */
	DELAYED_FOLLOW(0.1f),
	/** Makes the part act like a serpent making things like entity roll ripple through the body */
	SERPENTINE(2);

	float delay;
	ChainType(float delay) {
		this.delay = delay;
	}
	void setDelay(float delay) {
		this.delay = delay;
	}
	/** Higher = faster chase. */
	float getDelay() {
		return this.delay;
	}
}