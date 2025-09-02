package com.github.bea4dev.snowDawn.save

import com.github.bea4dev.snowDawn.item.ItemRegistry

object ServerData {
    val storyTextIndex = mutableMapOf<String, Int>()
    val craftableItems = mutableListOf(
        ItemRegistry.SCRAP_PIPE.id,
        ItemRegistry.SCRAP_PICKAXE.id,
        ItemRegistry.STONE_PICKAXE.id,
        ItemRegistry.TORCH.id,
        ItemRegistry.FURNACE.id,
        ItemRegistry.CAMPFIRE.id,
        ItemRegistry.FLINT_AND_STEEL.id,
    )
}