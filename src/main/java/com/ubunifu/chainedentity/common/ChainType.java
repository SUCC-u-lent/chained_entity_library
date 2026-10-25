package com.ubunifu.chainedentity.common;

/** The type of the chain, defined in the {@link com.ubunifu.chainedentity.common.ChainRoot} */
public enum ChainType {
	/** Makes this part lag behind its parent. */
	DELAYED_FOLLOW,
	/** Makes the part act like a IK part, allowing for dynamic limbs */
	INVERSE_KINEMATIC,
	/** Makes the part act like a serpent making things like entity roll ripple through the body */
	SERPENTINE
}
