package io.github.yuazer.zobbletrainerlevel.events

import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.api.LevelApi
import io.github.yuazer.zobbletrainerlevel.api.events.TrainerLevelUpEvent
import io.github.yuazer.zobbletrainerlevel.utils.PlayerUtils.runKether
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.asLangText

object PlayerEvent {
    @SubscribeEvent
    fun onLevelUp(event: TrainerLevelUpEvent){
        val player = event.player
        val level = event.level
        val maxLevel = ZobbleTrainerLevel.options.getInt("Level.maxLevel")
        if (LevelApi.getPlayerLevelContainer(player.name).level+level>maxLevel){
            player.sendMessage(player.asLangText("max-level-message"))
            event.isCancelled = true
        }
        val levelRewardList = ZobbleTrainerLevel.rewardConfig.getConfigurationSection("LevelReward")?.getKeys(false)
        if (!levelRewardList!!.contains(level.toString())){
            return
        }
        val rewardList = ZobbleTrainerLevel.rewardConfig.getStringList("LevelReward.$level")
        rewardList.runKether(player)


    }

}