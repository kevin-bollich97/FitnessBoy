package de.bollich.fitnessboy.ui.profile

import androidx.annotation.StringRes
import de.bollich.fitnessboy.model.UserProfile

data class ProfileFormState(
    val heightInput: String = "",
    @StringRes val heightErrorRes: Int? = null,
    val targetWeightInput: String = "",
    @StringRes val targetWeightErrorRes: Int? = null,
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(),
    val bmi: Double? = null,
    val form: ProfileFormState = ProfileFormState(),
)
