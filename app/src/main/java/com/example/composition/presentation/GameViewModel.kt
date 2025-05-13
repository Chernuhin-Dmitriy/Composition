package com.example.composition.presentation

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase
import java.util.Locale

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {
    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private lateinit var gameSettings: GameSettings
    private var timer: CountDownTimer? = null
    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _minCount = MutableLiveData<Int>()
    val minCount: LiveData<Int>
        get() = _minCount

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    init {
        startGame()
    }

    private fun startGame() {   // Получаем настройки игры
        getGameSettings()
        startTimer()
        generateQuestion()          // new Question in LiveData
        updateProgress()
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
        _minCount.value = gameSettings.minCountOfRightAnswers
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.getString(R.string.tv_progress),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCount.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        if(countOfQuestions == 0) {
            return 0
        }
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++

        }
        countOfQuestions++
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onFinish() {
                finishGame()
                _toastMessage.value = "Timer finish!"
            }

            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }
        }
        timer?.start()
    }

    fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughCount.value == true && enoughPercent.value == true,
            countOfQuestions,
            countOfRightAnswers,
            gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

//    lateinit var question: Question
//    var countOfRightAnswers: Int = 0
//    private var countOfAnswers = 0
//
//    private val _newQuestion = MutableLiveData<Boolean>()
//    val newQuestion: LiveData<Boolean>
//        get() = _newQuestion
//
//    private val _gameOver = MutableLiveData<Boolean>()
//    val gameOver: LiveData<Boolean>
//        get() = _gameOver
//
//    private val _toastMessage = MutableLiveData<String>()
//    val toastMessage: LiveData<String>
//        get() = _toastMessage
//
//    private val _gameTimeLeft = MutableLiveData<String>()
//    val gameTimeLeft: LiveData<String>
//        get() = _gameTimeLeft
//
//    private val _progressBar = MutableLiveData<Int>()
//    val progressBar: LiveData<Int>
//        get() = _progressBar
//
//
//    private fun generateQuestion(maxSumValue: Int): Question {
//        return generateQuestionUseCase(maxSumValue)
//    }

//    fun getGameSettings(level: Level): GameSettings {
//        return getGameSettingsUseCase(level)
//    }

//    fun checkAnswer(number: Int) {
//        if (number == question.sum - question.visibleNumber) {
//            countOfRightAnswers++
//        }
//        countOfAnswers++
//        _newQuestion.value = true
//

//    fun startTimer(level: Level) {
//        val roundTime = (getGameSettings(level).gameTimeInSeconds * 1000).toLong()
//        object : CountDownTimer((roundTime), 1000){
//            override fun onFinish() {
//                _toastMessage.value = "Finish timer!"
//                _gameOver.value = false
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                _gameTimeLeft.value = ("" + millisUntilFinished/1000)
//                _progressBar.value = millisUntilFinished.toInt()
//            }
//        }.start()
//    }

//    fun createQuestion(level: Level) {
//        if (getGameSettings(level).minCountOfRightAnswers <= countOfRightAnswers) {
//            _gameOver.value = true
//        }
//        question = generateQuestion(getGameSettings(level).maxSumValue)
//    }
//
//    fun gameOver(level: Level): GameResult {
//        return GameResult(
//            gameOver.value == true,
//            countOfAnswers,
//            countOfRightAnswers,
//            getGameSettings(level)
//        )
//    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }
}