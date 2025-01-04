package com.github.bea4dev.snowDawn.listeners

import com.github.bea4dev.snowDawn.biome.BiomeRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.biome.BiomeStore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

class ChunkListener: Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChunkLoad(event: ChunkLoadEvent) {
        if (event.world.name == "snow_land") {
            VanillaSourceAPI.getInstance().nmsHandler.setBiomeForChunk(event.chunk, BiomeRegistry.SNOW_LAND)
        }
    }
}