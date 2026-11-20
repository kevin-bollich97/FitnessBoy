package de.bollich.fitnessboy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HealthMetricsTest {
    @Test
    fun calculateBmi_returnsWeightDividedBySquaredHeight() {
        assertEquals(24.69, calculateBmi(80.0, 180.0)!!, 0.01)
    }

    @Test
    fun calculateBmi_returnsNullForMissingOrInvalidValues() {
        assertNull(calculateBmi(null, 180.0))
        assertNull(calculateBmi(80.0, null))
        assertNull(calculateBmi(80.0, 0.0))
        assertNull(calculateBmi(80.0, -170.0))
    }

    @Test
    fun classifyBmi_usesWhoCategories() {
        assertNull(classifyBmi(null))
        assertEquals("Untergewicht", classifyBmi(18.4))
        assertEquals("Normalgewicht", classifyBmi(18.5))
        assertEquals("Normalgewicht", classifyBmi(24.9))
        assertEquals("Übergewicht", classifyBmi(25.0))
        assertEquals("Adipositas Grad I", classifyBmi(30.0))
        assertEquals("Adipositas Grad II", classifyBmi(35.0))
        assertEquals("Adipositas Grad III", classifyBmi(40.0))
    }

    @Test
    fun calculateHealthyWeightRange_usesBmiBounds() {
        val range = calculateHealthyWeightRange(180.0)!!
        assertEquals(59.94, range.minimumInKg, 0.01)
        assertEquals(80.68, range.maximumInKg, 0.01)
    }

    @Test
    fun calculateOptimalWeight_usesBmi22() {
        assertEquals(71.28, calculateOptimalWeight(180.0)!!, 0.01)
    }

    @Test
    fun weightRangeCalculations_returnNullForMissingOrInvalidHeight() {
        assertNull(calculateHealthyWeightRange(null))
        assertNull(calculateHealthyWeightRange(0.0))
        assertNull(calculateOptimalWeight(null))
        assertNull(calculateOptimalWeight(-1.0))
    }
}
