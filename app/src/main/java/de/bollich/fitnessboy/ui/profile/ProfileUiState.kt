package de.bollich.fitnessboy.ui.profile

import androidx.annotation.StringRes
import de.bollich.fitnessboy.model.UserProfile

data class ProfileFormState(
    val heightInput: String = "",
    @StringRes val heightErrorRes: Int? = null,
    val targetWeightInput: String = "",
    @StringRes val targetWeightErrorRes: Int? = null,
    /** Shown after a successful save until the user edits the form again. */
    val isSaved: Boolean = false,
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(),
    val form: ProfileFormState = ProfileFormState(),
    /** Whether the inputs differ from the stored profile, i.e. saving would change something. */
    val hasUnsavedChanges: Boolean = false,
)
