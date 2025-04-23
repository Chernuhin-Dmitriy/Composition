package com.example.composition.domain.entity

data class GameResult (
    val winner: Boolean,
    val countOfQuestions: Int,
    val countOfRightAnswers: Int,
    val gameSettings: GameSettings  //Порог победы
)