package com.github.bea4dev.snowDawn.generator.structure

import org.bukkit.block.data.BlockData

interface Structures {
    fun getBlock(x: Int, structureY: Int, z: Int): BlockData?
}