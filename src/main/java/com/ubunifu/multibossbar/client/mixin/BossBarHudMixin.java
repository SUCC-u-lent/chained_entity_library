package com.ubunifu.multibossbar.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ubunifu.multibossbar.client.networking.ClientMultibarReciever;
import com.ubunifu.multibossbar.common.barhandler.ClientMultiBossBar;
import com.ubunifu.multibossbar.common.barhandler.Minibar;
import com.ubunifu.multibossbar.common.barhandler.MultiBossBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

	@Shadow
	@Final
	private static Identifier BARS_TEXTURE;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	Map<UUID, ClientBossBar> bossBars;

	@Inject(method = "render",at=@At("HEAD"))
	private void renderMixin(GuiGraphics graphics, CallbackInfo ci)
	{
		if (ClientMultibarReciever.multiBossBars.isEmpty()) {
			return;
		}

		int i = graphics.getScaledWindowWidth();
		int j = 12;
		if (!this.bossBars.isEmpty()) j = 12 + this.bossBars.size() * 19;
		ArrayList<ClientMultiBossBar> clientMultiBossBars = new ArrayList<>(ClientMultibarReciever.multiBossBars);
		for (ClientMultiBossBar multiBossBar : clientMultiBossBars) {
			int k = i / 2 - 91;
			this.mobettes$renderBar(graphics, k, j, multiBossBar);
			Text text = multiBossBar.getDisplayName();
			int m = this.client.textRenderer.getWidth(text);
			int n = i / 2 - m / 2;
			int o = j - 9;
			graphics.drawShadowedText(this.client.textRenderer, text, n, o, 16777215);
			int totalHeight = 0;
			for (Minibar minibar : multiBossBar.getMinibars()) {
				totalHeight += minibar.getScaleSupplier();
			}

			j += totalHeight;
			if (j >= graphics.getScaledWindowHeight() / 1.5) {
				break;
			}
		}
	}
	@Unique
	private void mobettes$renderBar(GuiGraphics graphics, int x, int y, ClientMultiBossBar bar) {
		List<Minibar> bars = new ArrayList<>(bar.getMinibars());
		var maxMinibars = bar.getMinibars().stream().max(Comparator.comparing(Minibar::getScaleSupplier));
		int maxScale = maxMinibars.map(Minibar::getScaleSupplier).orElse(5);
		int yOffset = 0;
		for (int index = 0; index < bars.size(); index++) {
			Minibar barInfo = bars.get(index);
			this.mobettes$renderBar(graphics, x, y+yOffset, barInfo, 182, 0);
			int i = (int)(bar.get(index).getPercent() * 183.0F);
			if (i > 0) {
				this.mobettes$renderBar(graphics, x, y+yOffset, barInfo, i, maxScale);
			}
			yOffset += barInfo.getScaleSupplier();
		}
	}

	@Unique
	private void mobettes$renderBar(GuiGraphics graphics, int x, int y, Minibar bar, int textureWidth, int vOffset) {
		graphics.drawTexture(BARS_TEXTURE, x, y, 0, bar.getColor().ordinal() * 5 * 2 + vOffset, textureWidth, bar.getScaleSupplier());
		if (bar.getStyle() != BossBar.Style.PROGRESS) {
			RenderSystem.enableBlend();
			graphics.drawTexture(BARS_TEXTURE, x, y, 0, 80 + (bar.getStyle().ordinal() - 1) * 5 * 2 + vOffset, textureWidth, bar.getScaleSupplier());
			RenderSystem.disableBlend();
		}
		graphics.getMatrices().push();
		graphics.getMatrices().scale(0.5f,0.5f,0.5f);
		graphics.drawShadowedText(
			this.client.textRenderer,
			bar.getDisplayText(),
			(x + 5) * 2,
			(y + 1) * 2,
			16777215
		);
		graphics.getMatrices().pop();
	}

	@Inject(method = "shouldPlayDragonMusic",at=@At("RETURN"),cancellable = true)
	private void playBossMusic(CallbackInfoReturnable<Boolean> cir)
	{
		ArrayList<ClientMultiBossBar> clientMultiBossBars = new ArrayList<>(ClientMultibarReciever.multiBossBars);
		if (clientMultiBossBars.isEmpty()) return;
		if (clientMultiBossBars.stream().anyMatch(MultiBossBar::isDragonMusic))
			cir.setReturnValue(true);
	}
	@Inject(method = "shouldDarkenSky",at=@At("RETURN"),cancellable = true)
	private void darkenSky(CallbackInfoReturnable<Boolean> cir)
	{
		ArrayList<ClientMultiBossBar> clientMultiBossBars = new ArrayList<>(ClientMultibarReciever.multiBossBars);
		if (clientMultiBossBars.isEmpty()) return;
		if (clientMultiBossBars.stream().anyMatch(MultiBossBar::isDarkenSky))
			cir.setReturnValue(true);
	}
	@Inject(method = "shouldThickenFog",at=@At("RETURN"),cancellable = true)
	private void thickenFog(CallbackInfoReturnable<Boolean> cir)
	{
		ArrayList<ClientMultiBossBar> clientMultiBossBars = new ArrayList<>(ClientMultibarReciever.multiBossBars);
		if (clientMultiBossBars.isEmpty()) return;
		if (clientMultiBossBars.stream().anyMatch(MultiBossBar::isThickenFog))
			cir.setReturnValue(true);
	}
}
