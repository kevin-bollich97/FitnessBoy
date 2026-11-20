package de.bollich.fitnessboy.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import de.bollich.fitnessboy.AppContainer
import de.bollich.fitnessboy.FitnessBoyApplication

/** Resolves the [AppContainer] inside a `viewModelFactory { initializer { ... } }` block. */
fun CreationExtras.appContainer(): AppContainer =
    (checkNotNull(this[APPLICATION_KEY]) as FitnessBoyApplication).container
