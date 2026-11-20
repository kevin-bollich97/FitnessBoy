package de.bollich.fitnessboy

import android.app.Application

class FitnessBoyApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
