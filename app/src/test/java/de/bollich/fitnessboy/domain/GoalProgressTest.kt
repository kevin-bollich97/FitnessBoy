package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class GoalProgressTest {
    private val start = LocalDate.of(2026, 1, 1)

    /** Entries newest first, as delivered by the repository. */
    private fun entries(vararg weights: Double): List<WeightEntry> =
        weights.mapIndexed { index, weight ->
            WeightEntry(date = start.plusDays(index.toLong()), weightInKg = weight)
        }.reversed()

    @Test
    fun returnsNullWithoutEntriesOrTarget() {
        assertNull(calculateGoalProgress(emptyList(), 70.0))
        assertNull(calculateGoalProgress(entries(80.0), null))
    }

    @Test
    fun losingWeight_inProgress() {
        val progress = calculateGoalProgress(entries(90.0, 85.0), targetWeightInKg = 80.0)!!

        assertEquals(GoalDirection.LOSE, progress.direction)
        assertEquals(GoalStatus.IN_PROGRESS, progress.status)
        assertEquals(90.0, progress.startWeightInKg, 0.0)
        assertEquals(85.0, progress.currentWeightInKg, 0.0)
        assertEquals(-5.0, progress.remainingInKg, 0.0)
        assertEquals(-5.0, progress.achievedInKg, 0.0)
        assertEquals(50, progress.progressPercent)
    }

    @Test
    fun losingWeight_reachedWhenBelowTarget() {
        val progress = calculateGoalProgress(entries(90.0, 79.0), targetWeightInKg = 80.0)!!

        assertEquals(GoalStatus.REACHED, progress.status)
        assertEquals(100, progress.progressPercent)
    }

    @Test
    fun gainingWeight_inProgress() {
        val progress = calculateGoalProgress(entries(60.0, 62.5), targetWeightInKg = 70.0)!!

        assertEquals(GoalDirection.GAIN, progress.direction)
        assertEquals(GoalStatus.IN_PROGRESS, progress.status)
        assertEquals(25, progress.progressPercent)
    }

    @Test
    fun gainingWeight_reachedWhenAboveTarget() {
        val progress = calculateGoalProgress(entries(60.0, 71.0), targetWeightInKg = 70.0)!!

        assertEquals(GoalStatus.REACHED, progress.status)
        assertEquals(100, progress.progressPercent)
    }

    @Test
    fun movingAwayFromGoal_clampsProgressToZero() {
        val progress = calculateGoalProgress(entries(90.0, 92.0), targetWeightInKg = 80.0)!!

        assertEquals(GoalStatus.IN_PROGRESS, progress.status)
        assertEquals(0, progress.progressPercent)
    }

    @Test
    fun targetEqualsStartWeight_isMaintainAndReached() {
        val progress = calculateGoalProgress(entries(80.0, 82.0), targetWeightInKg = 80.0)!!

        assertEquals(GoalDirection.MAINTAIN, progress.direction)
        assertEquals(GoalStatus.REACHED, progress.status)
        assertEquals(100, progress.progressPercent)
    }

    @Test
    fun singleEntry_usesItAsStartAndCurrent() {
        val progress = calculateGoalProgress(entries(85.0), targetWeightInKg = 80.0)!!

        assertEquals(85.0, progress.startWeightInKg, 0.0)
        assertEquals(85.0, progress.currentWeightInKg, 0.0)
        assertEquals(0, progress.progressPercent)
        assertEquals(GoalStatus.IN_PROGRESS, progress.status)
    }
}
