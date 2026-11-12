package net.fabricmc.fabric.api.client.rendering.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.DrawContext;

/** Rendu du HUD par-dessus celui de Minecraft (stub de compilation). */
public interface HudRenderCallback {
	Event<HudRenderCallback> EVENT = null;

	void onHudRender(DrawContext drawContext, float tickDelta);
}
