package de.bollich.fitnessboy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.model.AppSettings
import de.bollich.fitnessboy.ui.navigation.AppTab
import de.bollich.fitnessboy.ui.navigation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = true,
    val backStack: List<Destination> = backStackFor(startTab),
) {
    /** The screen currently shown. */
    val currentDestination: Destination
        get() = backStack.last()

    /** The tab highlighted in the bottom bar: the top-most tab on the back stack. */
    val selectedTab: AppTab
        get() = backStack.filterIsInstance<Destination.Tab>().last().tab
}

/**
 * Owns the navigation back stack and restores the last selected tab on start.
 *
 * Back stack shape follows Material guidance: the overview tab is the start destination and every
 * other tab sits on top of it, so system back from any tab returns to the overview.
 */
class MainViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.observeSettings().first()
            val tab = AppTab.entries.firstOrNull { it.name == settings.selectedTabName } ?: startTab
            _uiState.value = MainUiState(isLoading = false, backStack = backStackFor(tab))
        }
    }

    fun onTabSelected(tab: AppTab) {
        updateBackStack { backStackFor(tab) }
    }

    fun onBack() {
        updateBackStack { if (it.size > 1) it.dropLast(1) else it }
    }

    private fun updateBackStack(transform: (List<Destination>) -> List<Destination>) {
        val previousTab = _uiState.value.selectedTab
        _uiState.update { it.copy(backStack = transform(it.backStack)) }

        val selectedTab = _uiState.value.selectedTab
        if (selectedTab != previousTab) {
            viewModelScope.launch {
                settingsRepository.save(AppSettings(selectedTabName = selectedTab.name))
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { MainViewModel(appContainer().settingsRepository) }
        }
    }
}

private val startTab = AppTab.OVERVIEW

private fun backStackFor(tab: AppTab): List<Destination> =
    if (tab == startTab) {
        listOf(Destination.Tab(startTab))
    } else {
        listOf(Destination.Tab(startTab), Destination.Tab(tab))
    }
