package de.bollich.fitnessboy.ui.profile

import de.bollich.fitnessboy.model.UserProfile

data class ProfileFormState(
    val heightInput: String = "",
    val heightErrorText: String? = null,
    val targetWeightInput: String = "",
    val targetWeightErrorText: String? = null,
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(),
    val bmi: Double? = null,
    val form: ProfileFormState = ProfileFormState(),
)
