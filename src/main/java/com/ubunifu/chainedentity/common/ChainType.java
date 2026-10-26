package com.ubunifu.chainedentity.common;

/** The type of the chain, defined in the {@link com.ubunifu.chainedentity.common.ChainUnit} */
public enum ChainType {
	/** Makes this part lag behind its parent. */
	DELAYED_FOLLOW {
		@Override
		float getDelay() {
			return 0.1f;
		}
	},
	/** Makes the part act like a serpent making things like entity roll ripple through the body */
	SERPENTINE {
		@Override
		float getDelay() {
			return 2;
		}
	};

	/** Higher = faster chase. */
	abstract float getDelay();
}
