package com.example.composition.presentation

import android.os.CountDownTimer
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
    var countOfRightAnswers: Int = 0
    private var countOfAnswers = 0

    private val _newQuestion = MutableLiveData<Boolean>()
    val newQuestion: LiveData<Boolean>
        get() = _newQuestion

    private val _gameOver = MutableLiveData<Boolean>()
    val gameOver: LiveData<Boolean>
        get() = _gameOver

    private val _toastMessage = MutableLiveData<String>()
        val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _gameTimeLeft = MutableLiveData<String>()
    val gameTimeLeft: LiveData<String>
        get() = _gameTimeLeft

    private val _progressBar = MutableLiveData<Int>()
    val progressBar: LiveData<Int>
        get() = _progressBar


    private fun generateQuestion(maxSumValue: Int): Question {
        return generateQuestionUseCase(maxSumValue)
    }

    fun getGameSettings(level: Level): GameSettings {
        return getGameSettingsUseCase(level)
    }

    fun checkAnswer(number: Int) {
        if (number == question.sum - question.visibleNumber) {
            countOfRightAnswers++
        }
        countOfAnswers++
        _newQuestion.value = true
    }

    fun startTimer(level: Level) {
        val roundTime = (getGameSettings(level).gameTimeInSeconds * 1000).toLong()
        object : CountDownTimer((roundTime), 1000){
            override fun onFinish() {
                _toastMessage.value = "Finish timer!"
                _gameOver.value = false
            }

            override fun onTick(millisUntilFinished: Long) {
                _gameTimeLeft.value = ("" + millisUntilFinished/1000)
                _progressBar.value = millisUntilFinished.toInt()
            }
        }.start()
    }

    fun createQuestion(level: Level) {
        if (getGameSettings(level).minCountOfRightAnswers <= countOfRightAnswers) {
            _gameOver.value = true
        }
        question = generateQuestion(getGameSettings(level).maxSumValue)
    }

    fun gameOver(level: Level): GameResult {
        return GameResult(gameOver.value == true, countOfAnswers, countOfRightAnswers, getGameSettings(level))
    }

    companion object {
        private const val SECOND: Long = 1000
    }
}