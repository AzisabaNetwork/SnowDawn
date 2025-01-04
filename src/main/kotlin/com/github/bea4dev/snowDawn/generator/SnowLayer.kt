package com.github.bea4dev.snowDawn.generator

import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Snow

object SnowLayer {
    private val layers: List<BlockData>

    init {
        val layers = mutableListOf<BlockData>()
        for (i in 1..8) {
            val snow = Material.SNOW.createBlockData() as Snow
            snow.layers = i
            layers.add(snow)
        }
        this.layers = layers
    }

    operator fun get(index: Int): BlockData {
        return layers[index]
    }
}