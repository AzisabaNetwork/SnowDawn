package com.github.bea4dev.snowDawn.save

import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerDataRegistry {
    private val map = ConcurrentHashMap<UUID, PlayerData>()
    private val file = File("player_save.yml")
    private lateinit var yml: YamlConfiguration

    operator fun get(player: Player): PlayerData {
        return map.computeIfAbsent(player.uniqueId) { PlayerData(player) }
    }

    fun saveAll() {
        for (data in map.values) {
            data.save(yml)
        }
        yml.save(file)
    }

    fun loadAll() {
        if (!file.exists()) {
            file.createNewFile()
        }
        yml = YamlConfiguration.loadConfiguration(file)
    }

    fun load(playerData: PlayerData) {
        playerData.load(yml)
    }
}

class PlayerData(player: Player) {
    val uuid = player.uniqueId

    var finishedTutorial = false
    var respawnLocation = player.location.clone()
    var lastLocation = player.location.clone()
    var finishedSisetuMovie = false
    var prevSnowLandEntrance: Location? = null
    var secondMegaStructureEnterFlag = false

    fun save(yml: YamlConfiguration) {
        yml.set("$uuid.finishedTutorial", finishedTutorial)
        yml.set("$uuid.respawnLocation", respawnLocation)
        yml.set("$uuid.lastLocation", lastLocation)
        yml.set("$uuid.finishedSisetuMovie", finishedSisetuMovie)
        yml.set("$uuid.prevSnowLandEntrance", prevSnowLandEntrance)
        yml.set("$uuid.secondMegaStructureEnterFlag", secondMegaStructureEnterFlag)
    }

    fun load(yml: YamlConfiguration) {
        if (yml.contains(uuid.toString())) {
            finishedTutorial = yml.getBoolean("$uuid.finishedTutorial")
            respawnLocation = yml.getLocation("$uuid.respawnLocation")!!
            lastLocation = yml.getLocation("$uuid.lastLocation")!!
            finishedSisetuMovie = yml.getBoolean("$uuid.finishedSisetuMovie")
            prevSnowLandEntrance = yml.getLocation("$uuid.prevSnowLandEntrance")
            secondMegaStructureEnterFlag = yml.getBoolean("$uuid.secondMegaStructureEnterFlag")
        }
    }
}