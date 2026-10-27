package com.ubunifu.chainedentity.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ubunifu.chainedentity.common.ChainUnit;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderDispatcher.class)
public class ChainHitboxMixin {

	@Redirect(method = "renderHitbox",
	at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V",
		ordinal = 0
	))
	private static void changedParamaters(MatrixStack matrices,
	                                      VertexConsumer vertexConsumer,
	                                      Box box,
	                                      float red,
	                                      float green,
	                                      float blue,
	                                      float alpha,
	                                      @Local(name = "entity") Entity entity) {
		if (entity instanceof ChainUnit cu && cu.getParent() != null) {
			WorldRenderer.drawBox(matrices, vertexConsumer, box, 0.25f, 1f, 1f, 0.5f);
			return;
		}
		WorldRenderer.drawBox(matrices, vertexConsumer, box, red, green, blue, alpha);
	}
}
