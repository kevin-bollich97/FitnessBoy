package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.calculateHealthyWeightRange
import de.bollich.fitnessboy.domain.calculateOptimalWeight
import de.bollich.fitnessboy.domain.classifyBmi
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry

class GetHealthOverview {
    operator fun invoke(
        entries: List<WeightEntry>,
        profile: UserProfile,
    ): HealthOverview {
        val latestEntry = entries.firstOrNull()
        val previousEntry = entries.getOrNull(1)
        val trend = latestEntry?.let { latest ->
            previousEntry?.let { previous -> latest.weightInKg - previous.weightInKg }
        }
        val bmi = calculateBmi(latestEntry?.weightInKg, profile.heightInCm)
        val healthyWeightRange = calculateHealthyWeightRange(profile.heightInCm)
        val optimalWeight = calculateOptimalWeight(profile.heightInCm)

        return HealthOverview(
            latestEntry = latestEntry,
            trend = trend,
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
        val trend: Double?,
        val bmi: Double?,
        val bmiCategory: String?,
        val healthyWeightRangeText: String?,
        val optimalWeightText: String?,
    )
}
