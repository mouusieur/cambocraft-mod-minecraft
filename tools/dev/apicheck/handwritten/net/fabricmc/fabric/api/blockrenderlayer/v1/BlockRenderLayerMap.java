package net.fabricmc.fabric.api.blockrenderlayer.v1;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

/** Association bloc -> couche de rendu (stub de compilation). */
public interface BlockRenderLayerMap {
	BlockRenderLayerMap INSTANCE = null;

	void putBlock(Block block, RenderLayer renderLayer);

	void putBlocks(RenderLayer renderLayer, Block... blocks);
}
