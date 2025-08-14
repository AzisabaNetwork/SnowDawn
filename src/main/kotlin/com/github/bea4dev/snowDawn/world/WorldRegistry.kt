package com.github.bea4dev.snowDawn.world

import com.github.bea4dev.snowDawn.dimension.DimensionRegistry
import com.github.bea4dev.snowDawn.generator.GeneratorRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

object WorldRegistry {
    lateinit var ASSET: World
        private set
    lateinit var PROLOGUE: World
        private set
    lateinit var SNOW_LAND: World
        private set
    lateinit var SECOND_MEGA_STRUCTURE: World
        private set

    fun init() {
        Bukkit.getScheduler().runTask(VanillaSourceAPI.getInstance().plugin, Runnable {
            ASSET = Bukkit.getWorld("vanilla_source_assets_world")!!

            val voidWorldCreator = WorldCreator("prologue")
            voidWorldCreator.generator(GeneratorRegistry.VOID)
            PROLOGUE = Bukkit.createWorld(voidWorldCreator)!!
            VanillaSourceAPI.getInstance().nmsHandler.setDimensionType(PROLOGUE, DimensionRegistry.SNOW_LAND)

            deleteDirectory(Paths.get("snow_land"))
            deleteDirectory(Paths.get("second_mega_structure"))

            val slCreator = WorldCreator("snow_land")
            slCreator.generator(GeneratorRegistry.SNOW_LAND)
            SNOW_LAND = Bukkit.createWorld(slCreator)!!

            val smCreator = WorldCreator("second_mega_structure")
            smCreator.generator(GeneratorRegistry.SECOND_MEGA_STRUCTURE)
            SECOND_MEGA_STRUCTURE = Bukkit.createWorld(smCreator)!!

            val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler
            nmsHandler.setDimensionType(SNOW_LAND, DimensionRegistry.SNOW_LAND)
            nmsHandler.setDimensionType(SECOND_MEGA_STRUCTURE, DimensionRegistry.SECOND_MEGA_STRUCTURE)

            PROLOGUE.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            PROLOGUE.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            PROLOGUE.setStorm(false)

            SNOW_LAND.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            SNOW_LAND.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            SNOW_LAND.setGameRule(GameRule.KEEP_INVENTORY, true)
            SNOW_LAND.setStorm(true)

            SECOND_MEGA_STRUCTURE.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            SECOND_MEGA_STRUCTURE.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            SECOND_MEGA_STRUCTURE.setGameRule(GameRule.KEEP_INVENTORY, true)
        })
    }

    @Throws(IOException::class)
    private fun deleteDirectory(directory: Path) {
        if (!Files.exists(directory)) {
            return
        }

        Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            @Throws(IOException::class)
            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })
    }
}
