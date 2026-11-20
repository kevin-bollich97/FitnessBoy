package de.bollich.fitnessboy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Owns which tab and sub-page is shown and restores the last tab on start. */
class MainViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.observeSettings().first()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    selectedTab = settings.selectedTabName.toEnumOrDefault(AppTab.WEIGHT),
                    selectedWeightPage = settings.selectedWeightPageName.toEnumOrDefault(WeightPage.DASHBOARD),
                )
            }
        }
    }

    fun onTabSelected(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        persistSettings()
    }

    fun onWeightPageChange(page: WeightPage) {
        _uiState.update { it.copy(selectedWeightPage = page) }
        persistSettings()
    }

    fun onProfilePageChange(page: ProfilePage) {
        _uiState.update { it.copy(selectedProfilePage = page) }
    }

    private fun persistSettings() {
        val state = _uiState.value
        val settings = AppSettings(
            selectedTabName = state.selectedTab.name,
            selectedWeightPageName = state.selectedWeightPage.name,
        )
        viewModelScope.launch { settingsRepository.save(settings) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { MainViewModel(appContainer().settingsRepository) }
        }
    }
}

private inline fun <reified T : Enum<T>> String.toEnumOrDefault(default: T): T =
    enumValues<T>().firstOrNull { it.name == this } ?: default
