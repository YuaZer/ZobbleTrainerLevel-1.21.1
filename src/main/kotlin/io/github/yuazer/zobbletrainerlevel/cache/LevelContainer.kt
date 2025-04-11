package io.github.yuazer.zobbletrainerlevel.cache

import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.api.LevelApi
import io.github.yuazer.zobbletrainerlevel.api.events.TrainerExperienceGainEvent
import io.github.yuazer.zobbletrainerlevel.api.events.TrainerExperienceSetEvent
import io.github.yuazer.zobbletrainerlevel.api.events.TrainerLevelUpEvent
import io.github.yuazer.zobbletrainerlevel.utils.ScriptUtils
import org.bukkit.Bukkit
import taboolib.platform.compat.replacePlaceholder
import java.math.BigDecimal
import java.math.RoundingMode

class LevelContainer(
    val playerName: String,
    var level: Int,
    var experience: Int
) {



    // 增加等级
    fun addLevel(amount: Int) {
        this.level += amount
    }

    // 设置经验
    fun updateExperience(experience: Int) {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            val event = TrainerExperienceSetEvent(player, experience)
            Bukkit.getPluginManager().callEvent(event)
            if (event.isCancelled) return
            this.experience = event.experience
        } else {
            this.experience = experience
        }
        LevelApi.getLevelCache().put(playerName, this)
    }

    // 增加经验
    fun addExperience(amount: Int) {
        val player = Bukkit.getPlayer(playerName)
        var exp = amount

        if (player != null) {
            val event = TrainerExperienceGainEvent(player, amount)
            event.call()
            if (event.isCancelled) return
            exp = event.experienceGained
        }

        this.experience += exp
        LevelApi.getLevelCache().put(playerName,this)

        while (this.experience >= getExperienceForNextLevel()) {
            this.experience -= getExperienceForNextLevel()
            this.level++

            if (player != null) {
                val levelUpEvent = TrainerLevelUpEvent(player, this.level)
                levelUpEvent.call()
                if (levelUpEvent.isCancelled) return
                this.level = levelUpEvent.level
            }
        }

        LevelApi.getLevelCache().put(playerName,this)
    }

    // 计算升级所需经验
    fun getExperienceForNextLevel(): Int {
        var formula =
            ZobbleTrainerLevel.options.getString("Level.formula", "Math.pow((%level% + 1),2) * 100") ?: return 100
        formula = formula.replace("%level%", level.toString()).replace("^", "**")

        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            formula = formula.replacePlaceholder(player)
        }

        return try {
            ScriptUtils.evalToInt(formula)
        } catch (e: Exception) {
            Bukkit.getLogger().severe("§c[ZobbleTrainerLevel] 等级计算公式错误: $formula")
            e.printStackTrace()
            ((level + 1) * (level + 1) * 100)
        }
    }

    // 获取经验条百分比（0.0 ~ 100.0）
    fun getExperiencePercentageToLevelUp(): Double {
        val nextLevelExp = getExperienceForNextLevel()
        return if (nextLevelExp == 0) 0.0 else experience.toDouble() / nextLevelExp * 100.0
    }

    // 获取保留两位小数的百分比
    fun getExperiencePercentageToLevelUp_2scale(): Double {
        return BigDecimal(getExperiencePercentageToLevelUp()).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    override fun toString(): String {
        return "Player: $playerName, Level: $level, Experience: $experience, needExpToLevelUp: ${getExperienceForNextLevel()}"
    }
}
