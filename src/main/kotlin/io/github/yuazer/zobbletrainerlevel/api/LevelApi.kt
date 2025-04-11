package io.github.yuazer.zobbletrainerlevel.api

import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.cache.LevelContainer
import io.github.yuazer.zobbletrainerlevel.cache.LevelContainerCache

object LevelApi {
    fun getPlayerLevelContainer(playerName: String): LevelContainer {
        return ZobbleTrainerLevel.LEVEL_CACHE[playerName]
    }

    fun getLevelCache(): LevelContainerCache {
        return ZobbleTrainerLevel.LEVEL_CACHE
    }
}