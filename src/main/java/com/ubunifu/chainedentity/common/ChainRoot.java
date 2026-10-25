package com.ubunifu.chainedentity.common;

public interface ChainRoot extends ChainUnit {
	ChainType getChainType();
	ChainUnit[] getParts();
	/** This will change depending on what chain type. */
	Object[] getChainData();
}
