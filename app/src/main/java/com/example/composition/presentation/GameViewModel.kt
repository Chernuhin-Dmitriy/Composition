package com.example.composition.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel : ViewModel() {
    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    lateinit var question: Question
    private var countsOfRightAnswers: Int = 0
    lateinit var gameResult: GameResult

    private val _gameOver = MutableLiveData<Unit>()
    val gameOver: LiveData<Unit>
        get() = _gameOver

    private fun generateQuestion(maxSumValue: Int): Question {
        return generateQuestionUseCase(maxSumValue)
    }

    fun getGameSettings(level: Level): GameSettings {
        return getGameSettingsUseCase(level)
    }

    fun checkAnswer(number: Int) {
        if(number == question.sum - question.visibleNumber)
            countsOfRightAnswers++
    }

    // Тут генерируется каждый новый вопрос
    // Тут считается gameResult
    // Тут

    fun gameStart(level: Level) {
        question = generateQuestion(getGameSettings(level).maxSumValue)
        startTimer()

    }
}