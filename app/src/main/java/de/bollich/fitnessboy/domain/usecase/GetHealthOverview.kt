package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.WeightRange
import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.calculateGoalProgress
import de.bollich.fitnessboy.domain.calculateHealthyWeightRange
import de.bollich.fitnessboy.domain.calculateOptimalWeight
import de.bollich.fitnessboy.domain.calculateWeightTrend
import de.bollich.fitnessboy.domain.classifyBmi
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry

class GetHealthOverview {
    operator fun invoke(
        entries: List<WeightEntry>,
        profile: UserProfile,
    ): HealthOverview {
        val latestEntry = entries.firstOrNull()
        val bmi = calculateBmi(latestEntry?.weightInKg, profile.heightInCm)
        val goalProgress = calculateGoalProgress(entries, profile.targetWeightInKg)
        val trends = WeightTrendPeriod.entries
            .mapNotNull { period -> calculateWeightTrend(entries, period) }

        return HealthOverview(
            latestEntry = latestEntry,
            trends = trends,
            goalProgress = goalProgress,
            bmi = bmi,
            bmiCategory = classifyBmi(bmi),
            healthyWeightRange = calculateHealthyWeightRange(profile.heightInCm),
            optimalWeightInKg = calculateOptimalWeight(profile.heightInCm),
        )
    }

    data class HealthOverview(
        val latestEntry: WeightEntry?,
        val trends: List<WeightTrend>,
        val goalProgress: GoalProgress?,
        val bmi: Double?,
        val bmiCategory: BmiCategory?,
        val healthyWeightRange: WeightRange?,
        val optimalWeightInKg: Double?,
    )
}
