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
    lateinit var VOID: World
        private set
    lateinit var SNOW_LAND: World
        private set

    fun init() {
        Bukkit.getScheduler().runTask(VanillaSourceAPI.getInstance().plugin, Runnable {
            val voidWorldCreator = WorldCreator("prologue")
            voidWorldCreator.generator(GeneratorRegistry.VOID)
            VOID = Bukkit.createWorld(voidWorldCreator)!!
            VanillaSourceAPI.getInstance().nmsHandler.setDimensionType(VOID, DimensionRegistry.SNOW_LAND_DIMENSION)


            val directory = Paths.get("snow_land")
            deleteDirectory(directory)

            val creator = WorldCreator("snow_land")
            creator.generator(GeneratorRegistry.SNOW_LAND)
            SNOW_LAND = Bukkit.createWorld(creator)!!

            VanillaSourceAPI.getInstance().nmsHandler.setDimensionType(SNOW_LAND, DimensionRegistry.SNOW_LAND_DIMENSION)

            SNOW_LAND.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            SNOW_LAND.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            SNOW_LAND.setStorm(true)
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