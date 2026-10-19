package com.ubunifu.multibossbar.client.mixin;

import net.minecraft.client.gui.*;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
	@Accessor("guiAtlasManager")
	GuiAtlasManager multibossbar$getManager();
    @Invoker("drawSprite")
    void multibossbar$invokeDrawSprite(
        Sprite sprite,
        int x,
        int y,
        int z,
        int width,
        int height
    );
}
