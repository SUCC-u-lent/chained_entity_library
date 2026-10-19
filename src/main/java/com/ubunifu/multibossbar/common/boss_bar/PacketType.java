package com.ubunifu.multibossbar.common.boss_bar;

public enum PacketType {
	CREATE_BAR(false),
	REMOVE_BAR(true),
	ADD_BAR(true),
	UPDATE_NAME(true),
	UPDATE_STYLE(true),
    UPDATE_PERCENT(true),
	UPDATE_COLOR(true),
	UPDATE_DARKEN_SKY(true),
	UPDATE_DRAGON_MUSIC(true),
	UPDATE_THICKEN_FOG(true),
	CLEAR_BARS(false),
	SEND_ALL_BARS(false),
	SET_BAR_SCALE(true),
	UPDATE_SECTION_TYPE(true),
	RESYNC(false),
	UPDATE_VISIBILITY(false),
	UPDATE_CONTAINER_NAME(false);

	final boolean barUpdate;
	PacketType(boolean barUpdate) {
		this.barUpdate = barUpdate;
	}

	public boolean isBarUpdate() { return this.barUpdate; }
}
