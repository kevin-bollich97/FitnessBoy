package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.calculateGoalProgress
import de.bollich.fitnessboy.domain.calculateHealthyWeightRange
import de.bollich.fitnessboy.domain.calculateOptimalWeight
import de.bollich.fitnessboy.domain.calculateWeightTrend
import de.bollich.fitnessboy.domain.classifyBmi
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry

class GetHealthOverview {
    operator fun invoke(
        entries: List<WeightEntry>,
        profile: UserProfile,
    ): HealthOverview {
        val latestEntry = entries.firstOrNull()
        val bmi = calculateBmi(latestEntry?.weightInKg, profile.heightInCm)
        val healthyWeightRange = calculateHealthyWeightRange(profile.heightInCm)
        val optimalWeight = calculateOptimalWeight(profile.heightInCm)
        val goalProgress = calculateGoalProgress(entries, profile.targetWeightInKg)
        val trends = WeightTrendPeriod.entries
            .mapNotNull { period -> calculateWeightTrend(entries, period) }
        val primaryTrend = trends.firstOrNull { it.period == WeightTrendPeriod.SEVEN_DAYS }
            ?: trends.firstOrNull { it.period == WeightTrendPeriod.THIRTY_DAYS }
            ?: trends.firstOrNull()

        return HealthOverview(
            latestEntry = latestEntry,
            primaryTrend = primaryTrend,
            trends = trends,
            goalProgress = goalProgress,
            bmi = bmi,
            bmiCategory = classifyBmi(bmi),
            healthyWeightRangeText = healthyWeightRange?.let {
                "${formatNumber(it.minimumInKg)} bis ${formatNumber(it.maximumInKg)} kg"
            },
            optimalWeightText = optimalWeight?.let { "${formatNumber(it)} kg" },
        )
    }

    data class HealthOverview(
        val latestEntry: WeightEntry?,
        val primaryTrend: WeightTrend?,
        val trends: List<WeightTrend>,
        val goalProgress: GoalProgress?,
        val bmi: Double?,
        val bmiCategory: String?,
        val healthyWeightRangeText: String?,
        val optimalWeightText: String?,
    )
}
