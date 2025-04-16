package io.github.yuazer.zobbletrainerlevel

import com.cobblemon.mod.common.util.server
import io.github.yuazer.zobbletrainerlevel.cache.LevelContainerCache
import io.github.yuazer.zobbletrainerlevel.events.CobbleEventHandler
import net.minecraft.server.MinecraftServer
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.BukkitPlugin

object ZobbleTrainerLevel : Plugin() {
    @Config("config.yml")
    lateinit var config: ConfigFile

    @Config("options.yml")
    lateinit var options: ConfigFile

    @Config("level.yml")
    lateinit var levelConfig: ConfigFile

    @Config("reward.yml")
    lateinit var rewardConfig: ConfigFile

    //    val LEVEL_CACHE: LevelContainerCache by lazy {
//        LevelContainerCache().apply {
//            loadAll()
//        }
//    }
    val LEVEL_CACHE: LevelContainerCache = LevelContainerCache().apply {
        loadAll()
    }
    lateinit var server: MinecraftServer
    override fun onEnable() {

    }

    @Awake(LifeCycle.ENABLE)
    fun loadPlugin() {
        CobbleEventHandler.registerCobbleEvent()
        logLoaded()
    }

    @Awake(LifeCycle.DISABLE)
    fun disablePlugin() {
        LEVEL_CACHE.saveAll()
    }

    override fun onDisable() {

    }

    private fun logLoaded() {
        val description = BukkitPlugin.getInstance().description
        val version = description.version
        val pluginName = description.name

        info(
            """
            
        §e══════════════════════════════════════════════
        §e|      §b ________        __       ___  ___    §e|
        §e|      §b("      "\      /""\     |"  \/"  |   §e|
        §e|      §b \___/   :)    /    \     \   \  /    §e|
        §e|      §b   /  ___/    /' /\  \     \\  \/     §e|
        §e|      §b  //  \__    //  __'  \    /\.  \     §e|
        §e|      §b (:   / "\  /   /  \\  \  /  \   \    §e|
        §e|      §b  \_______)(___/    \___)|___/\___|   §e|
        §e══════════════════════════════════════════════
        §6[§eZAX§6] §aPlugin: §b$pluginName
        §6[§eZAX§6] §aVersion: §e$version
        §6[§eZAX§6] §aAuthor: §bZ菌[QQ:1109132]
        §e══════════════════════════════════════════════
    """.trimIndent()
        )
    }
}