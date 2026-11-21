package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.testing.FakeBodyMeasurementsRepository
import de.bollich.fitnessboy.testing.FakeProfileRepository
import de.bollich.fitnessboy.testing.FakeWeightRepository
import de.bollich.fitnessboy.testing.MainDispatcherRule
import de.bollich.fitnessboy.ui.overview.OverviewViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class OverviewViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weightRepository = FakeWeightRepository()
    private val profileRepository = FakeProfileRepository()
    private val bodyMeasurementsRepository = FakeBodyMeasurementsRepository()
    private val viewModel by lazy {
        OverviewViewModel(
            weightRepository = weightRepository,
            profileRepository = profileRepository,
            bodyMeasurementsRepository = bodyMeasurementsRepository,
            getHealthOverview = GetHealthOverview(),
        )
    }

    private fun TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private suspend fun addWeight(date: LocalDate, weightInKg: Double) {
        weightRepository.add(WeightEntry(date = date, weightInKg = weightInKg))
    }

    @Test
    fun uiState_combinesWeightProfileAndMeasurements() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0, targetWeightInKg = 75.0))
        addWeight(LocalDate.of(2026, 3, 1), 85.0)
        addWeight(LocalDate.of(2026, 3, 31), 81.0)
        val measurements = BodyMeasurementsEntry(
            date = LocalDate.of(2026, 3, 30),
            waistInCm = 90.0,
            hipsInCm = 100.0,
            shouldersInCm = 50.0,
        )
        bodyMeasurementsRepository.add(measurements)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(81.0, state.latestWeight!!.weightInKg, 0.0)
        assertEquals(BmiCategory.OVERWEIGHT, state.bmiCategory)
        assertTrue(state.hasTargetWeight)
        assertEquals(75.0, state.goalProgress!!.targetWeightInKg, 0.0)
        assertEquals(measurements.date, state.latestMeasurements!!.date)
    }

    @Test
    fun recentTrend_isShownWhenItDiffersFromTotalTrend() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 85.0)
        addWeight(LocalDate.of(2026, 3, 20), 83.0)
        addWeight(LocalDate.of(2026, 3, 31), 81.0)

        val state = viewModel.uiState.value
        assertEquals(WeightTrendPeriod.SEVEN_DAYS, state.recentTrend!!.period)
        assertEquals(LocalDate.of(2026, 3, 20), state.recentTrend!!.referenceDate)
        assertEquals(LocalDate.of(2026, 3, 1), state.totalTrend!!.referenceDate)
    }

    @Test
    fun recentTrend_isHiddenWhenItWouldRepeatTotalTrend() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 85.0)
        addWeight(LocalDate.of(2026, 3, 31), 81.0)

        val state = viewModel.uiState.value
        assertNull(state.recentTrend)
        assertEquals(-4.0, state.totalTrend!!.deltaInKg, 0.0)
    }

    @Test
    fun emptyData_hasNoValuesAndNoTarget() = runTest {
        collectUiState()

        val state = viewModel.uiState.value
        assertNull(state.latestWeight)
        assertNull(state.totalTrend)
        assertFalse(state.hasTargetWeight)
        assertNull(state.bmi)
        assertNull(state.latestMeasurements)
    }

    @Test
    fun bmiInfoVisibility_followsUserAction() = runTest {
        collectUiState()

        viewModel.onBmiInfoVisibilityChange(true)
        assertTrue(viewModel.uiState.value.isBmiInfoVisible)

        viewModel.onBmiInfoVisibilityChange(false)
        assertFalse(viewModel.uiState.value.isBmiInfoVisible)
    }
}
