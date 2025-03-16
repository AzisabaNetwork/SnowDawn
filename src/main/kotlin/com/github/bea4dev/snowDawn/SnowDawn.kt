package com.github.bea4dev.snowDawn

import com.github.bea4dev.snowDawn.biome.BiomeRegistry
import com.github.bea4dev.snowDawn.dimension.DimensionRegistry
import com.github.bea4dev.snowDawn.generator.GeneratorRegistry
import com.github.bea4dev.snowDawn.listeners.ChunkListener
import com.github.bea4dev.snowDawn.listeners.PlayerJoinQuitListener
import com.github.bea4dev.snowDawn.listeners.WeaponListener
import com.github.bea4dev.snowDawn.player.PlayerManagerListener
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import com.github.shynixn.mccoroutine.bukkit.ShutdownStrategy
import com.github.shynixn.mccoroutine.bukkit.mcCoroutineConfiguration
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.SpigotConfig
import java.time.Duration

class SnowDawn : JavaPlugin() {

    companion object {
        lateinit var ENTITY_THREAD: TickThread
            private set
        lateinit var plugin: SnowDawn
            private set
    }

    override fun onEnable() {
        plugin = this

        ENTITY_THREAD = VanillaSourceAPI.getInstance().tickThreadPool.nextTickThread

        DimensionRegistry.init()
        BiomeRegistry.init()
        GeneratorRegistry.init(0)
        WorldRegistry.init()

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(PlayerJoinQuitListener(), this)
        pluginManager.registerEvents(ChunkListener(), this)
        pluginManager.registerEvents(PlayerManagerListener(), this)
        pluginManager.registerEvents(WeaponListener(), this)

        SpigotConfig.disabledAdvancements.add("*")

        this.mcCoroutineConfiguration.shutdownStrategy = ShutdownStrategy.MANUAL
    }

    override fun onDisable() {
        // Plugin shutdown logic

        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(
                Component.text("[Server] ")
                    .append(Component.text("Starting shutdown...").color(NamedTextColor.YELLOW))
            )
        }

        // shutdown coroutine jobs
        runBlocking {
            withTimeout(Duration.ofSeconds(10).toMillis()) {
                this@SnowDawn.scope.coroutineContext[Job]!!.children.forEach { childJob ->
                    childJob.join()
                }
            }
        }

        this.mcCoroutineConfiguration.disposePluginSession()
    }
}
