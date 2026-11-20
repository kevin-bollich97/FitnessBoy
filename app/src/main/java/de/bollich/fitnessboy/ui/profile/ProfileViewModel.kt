package de.bollich.fitnessboy.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.domain.usecase.SaveProfile
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.ui.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    weightRepository: WeightRepository,
    private val saveProfile: SaveProfile,
) : ViewModel() {
    /** Null until prefilled from the stored profile; afterwards the form belongs to the user. */
    private val form = MutableStateFlow<ProfileFormState?>(null)

    val uiState: StateFlow<ProfileUiState> =
        combine(
            profileRepository.observeProfile(),
            weightRepository.observeEntries(),
            form.filterNotNull(),
        ) { profile, entries, form ->
            ProfileUiState(
                isLoading = false,
                profile = profile,
                bmi = calculateBmi(entries.firstOrNull()?.weightInKg, profile.heightInCm),
                form = form,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState(),
        )

    init {
        viewModelScope.launch {
            val profile = profileRepository.observeProfile().first()
            form.value = profile.toFormState()
        }
    }

    fun onHeightValueChange(value: String) {
        form.update { it?.copy(heightInput = value, heightErrorRes = null) }
    }

    fun onTargetWeightValueChange(value: String) {
        form.update { it?.copy(targetWeightInput = value, targetWeightErrorRes = null) }
    }

    fun onSaveClick() {
        val current = form.value ?: return
        viewModelScope.launch {
            val result = saveProfile(
                profile = profileRepository.observeProfile().first(),
                heightInput = current.heightInput,
                targetWeightInput = current.targetWeightInput,
            )
            when (result) {
                is SaveProfile.Result.Success -> form.value = result.profile.toFormState()
                SaveProfile.Result.InvalidHeight -> form.update {
                    it?.copy(heightErrorRes = R.string.error_invalid_height, targetWeightErrorRes = null)
                }
                SaveProfile.Result.InvalidTargetWeight -> form.update {
                    it?.copy(targetWeightErrorRes = R.string.error_invalid_target_weight)
                }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                ProfileViewModel(
                    profileRepository = container.profileRepository,
                    weightRepository = container.weightRepository,
                    saveProfile = SaveProfile(container.profileRepository),
                )
            }
        }
    }
}

private fun UserProfile.toFormState() = ProfileFormState(
    heightInput = heightInCm?.let(::formatNumber).orEmpty(),
    targetWeightInput = targetWeightInKg?.let(::formatNumber).orEmpty(),
)
