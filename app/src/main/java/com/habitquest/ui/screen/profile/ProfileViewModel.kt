package com.habitquest.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvatarOption(val emoji: String, val label: String)

val AVATAR_OPTIONS = listOf(
    AvatarOption("🐱", "Gato"),
    AvatarOption("🐈", "Gata"),
    AvatarOption("🐕", "Perro"),
    AvatarOption("🐩", "Perra"),
    AvatarOption("🦸", "Humano"),
    AvatarOption("🧝", "Humana"),
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val totalXP: Int = 0,
        val currentLevel: Level = Levels.all.first(),
        val userName: String = "Héroe",
        val userAvatar: String = "🦸",
        val isEditingName: Boolean = false,
        val editNameDraft: String = "",
        val showAvatarPicker: Boolean = false,
        val notificationsEnabled: Boolean = false,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getUserStats()
            ) { habits, stats -> habits to stats }
                .collect { (habits, stats) ->
                    _uiState.update {
                        it.copy(
                            habits = habits,
                            totalXP = stats.totalXP,
                            currentLevel = Levels.getCurrentLevel(stats.totalXP),
                            userName = stats.userName,
                            userAvatar = stats.userAvatar,
                            editNameDraft = if (it.isEditingName) it.editNameDraft else stats.userName,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun startEditingName() {
        _uiState.update { it.copy(isEditingName = true, editNameDraft = it.userName) }
    }

    fun updateNameDraft(name: String) {
        _uiState.update { it.copy(editNameDraft = name) }
    }

    fun confirmEditName() {
        val name = _uiState.value.editNameDraft.trim().ifEmpty { "Héroe" }
        _uiState.update { it.copy(isEditingName = false, userName = name) }
        viewModelScope.launch { repository.updateUserName(name) }
    }

    fun cancelEditName() {
        _uiState.update { it.copy(isEditingName = false) }
    }

    fun openAvatarPicker() {
        _uiState.update { it.copy(showAvatarPicker = true) }
    }

    fun selectAvatar(emoji: String) {
        _uiState.update { it.copy(userAvatar = emoji, showAvatarPicker = false) }
        viewModelScope.launch { repository.updateUserAvatar(emoji) }
    }

    fun dismissAvatarPicker() {
        _uiState.update { it.copy(showAvatarPicker = false) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }
}
