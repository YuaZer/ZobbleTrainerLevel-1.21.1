package io.github.yuazer.zobbletrainerlevel.cache

import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.concurrent.ConcurrentHashMap

class LevelContainerCache {

    private val cache = ConcurrentHashMap<String, LevelContainer>()
    private val levelDataFile = newFile(getDataFolder(),"level.yml",true)
    private val levelDataYaml = Configuration.loadFromFile(levelDataFile, Type.YAML)

    /**
     * 获取玩家等级容器
     */
    operator fun get(playerName: String): LevelContainer {
        return cache[playerName]
            ?: if (levelDataYaml.contains(playerName)) {
                val loaded = loadFromYaml(playerName)
                cache[playerName] = loaded
                loaded
            } else {
                LevelContainer(playerName, 1, 0)
            }
    }

    /**
     * 设置或添加玩家等级容器
     */
    fun put(playerName: String, levelContainer: LevelContainer) {
        cache[playerName] = levelContainer
    }

    /**
     * 移除某个玩家的数据（包含缓存和 YAML）
     */
    fun remove(playerName: String) {
        cache.remove(playerName)
        levelDataYaml[playerName] = null
        saveYaml()
    }

    /**
     * 清空所有缓存和配置文件中的内容
     */
    fun clear() {
        cache.clear()
        levelDataYaml[""] = null
        saveYaml()
    }

    /**
     * 从 level.yml 加载所有数据进缓存
     */
    fun loadAll() {
        for (playerName in levelDataYaml.getKeys(false)) {
            val levelContainer = loadFromYaml(playerName)
            cache[playerName] = levelContainer
        }
    }

    /**
     * 保存所有缓存数据到 YAML 文件
     */
    fun saveAll() {
        for ((playerName, levelContainer) in cache) {
            saveToYaml(playerName, levelContainer)
        }
        saveYaml()
    }

    /**
     * 从 YAML 中加载某一玩家数据
     */
    private fun loadFromYaml(playerName: String): LevelContainer {
        val level = levelDataYaml.getInt("$playerName.level", 1)
        val experience = levelDataYaml.getInt("$playerName.experience", 0)
        return LevelContainer(playerName, level, experience)
    }

    /**
     * 将某个玩家的数据写入 YAML 对象
     */
    private fun saveToYaml(playerName: String, levelContainer: LevelContainer) {
        levelDataYaml["$playerName.level"] = levelContainer.level
        levelDataYaml["$playerName.experience"] = levelContainer.experience
    }

    /**
     * 保存 YAML 对象到文件
     */
    private fun saveYaml() {
        try {
            levelDataYaml.saveToFile(levelDataFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
