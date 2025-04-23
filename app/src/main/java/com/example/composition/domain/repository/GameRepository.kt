package com.example.composition.domain.repository

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question

interface GameRepository {

    fun getGameSettings(level: Level): GameSettings

    fun generateQuestions(
        maxSumValue: Int,
        countOfOptions: Int
    ): Question
}