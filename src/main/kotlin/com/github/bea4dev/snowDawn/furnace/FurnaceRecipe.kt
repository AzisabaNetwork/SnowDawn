package com.github.bea4dev.snowDawn.furnace

import com.github.bea4dev.snowDawn.item.ItemRegistry
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.RecipeChoice

object FurnaceRecipe {
    fun init() {
        val fertilizerRecipe = FurnaceRecipe(
            NamespacedKey.minecraft("fertilizer"),
            ItemRegistry.FERTILIZER.createItemStack(),
            RecipeChoice.ExactChoice(ItemRegistry.CATALYST.createItemStack()),
            0.0F,
            100
        )
        Bukkit.getServer().addRecipe(fertilizerRecipe)
    }
}