package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.WeightEntry

enum class GoalDirection {
    LOSE,
    GAIN,
    MAINTAIN,
}

enum class GoalStatus {
    NOT_STARTED,
    IN_PROGRESS,
    REACHED,
}

data class GoalProgress(
    val startWeightInKg: Double,
    val currentWeightInKg: Double,
    val targetWeightInKg: Double,
    val remainingInKg: Double,
    val achievedInKg: Double,
    val progressPercent: Int,
    val direction: GoalDirection,
    val status: GoalStatus,
)

fun calculateGoalProgress(
    entries: List<WeightEntry>,
    targetWeightInKg: Double?,
): GoalProgress? {
    val currentEntry = entries.firstOrNull() ?: return null
    val startEntry = entries.lastOrNull() ?: return null
    val targetWeight = targetWeightInKg ?: return null

    val startWeight = startEntry.weightInKg
    val currentWeight = currentEntry.weightInKg
    val totalDistance = targetWeight - startWeight
    val travelledDistance = currentWeight - startWeight
    val remaining = targetWeight - currentWeight

    val direction = when {
        totalDistance < 0 -> GoalDirection.LOSE
        totalDistance > 0 -> GoalDirection.GAIN
        else -> GoalDirection.MAINTAIN
    }

    val progressPercent = when {
        totalDistance == 0.0 -> 100
        else -> ((travelledDistance / totalDistance) * 100)
            .toInt()
            .coerceIn(0, 100)
    }

    val status = when {
        direction == GoalDirection.MAINTAIN -> GoalStatus.REACHED
        remaining == 0.0 -> GoalStatus.REACHED
        direction == GoalDirection.LOSE && currentWeight <= targetWeight -> GoalStatus.REACHED
        direction == GoalDirection.GAIN && currentWeight >= targetWeight -> GoalStatus.REACHED
        else -> GoalStatus.IN_PROGRESS
    }

    return GoalProgress(
        startWeightInKg = startWeight,
        currentWeightInKg = currentWeight,
        targetWeightInKg = targetWeight,
        remainingInKg = remaining,
        achievedInKg = travelledDistance,
        progressPercent = progressPercent,
        direction = direction,
        status = status,
    )
}
