package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class GetHealthOverviewTest {
    private val getHealthOverview = GetHealthOverview()

    /** Newest first, like the repository delivers them. */
    private val entries = listOf(
        WeightEntry(id = 3, date = LocalDate.of(2026, 3, 31), weightInKg = 80.0),
        WeightEntry(id = 2, date = LocalDate.of(2026, 3, 20), weightInKg = 82.0),
        WeightEntry(id = 1, date = LocalDate.of(2026, 3, 1), weightInKg = 85.0),
    )

    @Test
    fun completeProfile_derivesAllValuesFromLatestEntry() {
        val overview = getHealthOverview(entries, UserProfile(heightInCm = 180.0, targetWeightInKg = 75.0))

        assertEquals(entries.first(), overview.latestEntry)
        assertEquals(24.69, overview.bmi!!, 0.01)
        assertEquals(BmiCategory.NORMAL_WEIGHT, overview.bmiCategory)
        assertEquals(59.94, overview.healthyWeightRange!!.minimumInKg, 0.01)
        assertEquals(80.68, overview.healthyWeightRange!!.maximumInKg, 0.01)
        assertEquals(71.28, overview.optimalWeightInKg!!, 0.01)
        assertEquals(75.0, overview.goalProgress!!.targetWeightInKg, 0.0)
        assertEquals(
            listOf(WeightTrendPeriod.SEVEN_DAYS, WeightTrendPeriod.THIRTY_DAYS, WeightTrendPeriod.SINCE_START),
            overview.trends.map { it.period },
        )
    }

    @Test
    fun missingHeight_leavesHeightBasedValuesEmpty() {
        val overview = getHealthOverview(entries, UserProfile(targetWeightInKg = 75.0))

        assertNull(overview.bmi)
        assertNull(overview.bmiCategory)
        assertNull(overview.healthyWeightRange)
        assertNull(overview.optimalWeightInKg)
        assertNotNull(overview.goalProgress)
    }

    @Test
    fun missingTargetWeight_hasNoGoalProgress() {
        val overview = getHealthOverview(entries, UserProfile(heightInCm = 180.0))

        assertNull(overview.goalProgress)
        assertNotNull(overview.bmi)
    }

    @Test
    fun noEntries_onlyHeightBasedValuesAreAvailable() {
        val overview = getHealthOverview(emptyList(), UserProfile(heightInCm = 180.0, targetWeightInKg = 75.0))

        assertNull(overview.latestEntry)
        assertNull(overview.bmi)
        assertNull(overview.goalProgress)
        assertEquals(emptyList<Any>(), overview.trends)
        assertNotNull(overview.healthyWeightRange)
        assertNotNull(overview.optimalWeightInKg)
    }
}
