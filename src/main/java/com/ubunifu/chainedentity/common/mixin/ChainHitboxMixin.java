package com.ubunifu.chainedentity.common.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderDispatcher.class)
public class ChainHitboxMixin {

	@ModifyArgs(method = "renderHitbox",
	at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V",
		ordinal = 0
	))
	private static void changedParamaters(Args args) {
		args.set(4, 0.25f);
		args.set(5, 1.0f);
		args.set(6, 1.0f);
		args.set(7, 1.0f);
	}
}
