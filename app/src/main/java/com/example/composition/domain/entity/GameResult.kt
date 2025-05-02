package com.example.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class GameResult (
    val winner: Boolean,
    val countOfQuestions: Int,
    val countOfRightAnswers: Int,
    val gameSettings: GameSettings  //Порог победы
) : Parcelable