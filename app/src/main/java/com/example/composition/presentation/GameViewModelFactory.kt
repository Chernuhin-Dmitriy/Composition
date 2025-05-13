package com.example.composition.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.composition.domain.entity.Level

class GameViewModelFactory(
    private val level: Level,
    private val application: Application
) : ViewModelProvider.Factory {

    // ViewModelFactory нужна чтобы передать в конструктор viewModel какие либо параметры
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){ // Проверяем что это именно GameViewModel
            return GameViewModel(application, level) as T
        }
        throw RuntimeException("Unknow view model class $modelClass")
    }
}