package de.bollich.fitnessboy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.data.ProfileStore
import de.bollich.fitnessboy.data.WeightStore
import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.calculateHealthyWeightRange
import de.bollich.fitnessboy.domain.calculateOptimalWeight
import de.bollich.fitnessboy.domain.classifyBmi
import de.bollich.fitnessboy.domain.parseHeight
import de.bollich.fitnessboy.domain.parseOptionalWeight
import de.bollich.fitnessboy.domain.parseWeight
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.AddWeightCard
import de.bollich.fitnessboy.ui.components.BmiInsightsCard
import de.bollich.fitnessboy.ui.components.EmptyStateCard
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.components.MetricInfoCard
import de.bollich.fitnessboy.ui.components.ProfileCard
import de.bollich.fitnessboy.ui.components.SummaryCard
import de.bollich.fitnessboy.ui.components.WeightChartCard
import de.bollich.fitnessboy.ui.components.WeightHistoryRow
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

private enum class AppTab(
    val label: String,
    val iconRes: Int,
) {
    WEIGHT("Gewicht", R.drawable.ic_home),
    BMI("BMI", R.drawable.ic_favorite),
    PROFILE("Profil", R.drawable.ic_account_box),
}

private enum class WeightPage {
    DASHBOARD,
    HISTORY,
}

@Composable
fun FitnessBoyApp() {
    val context = LocalContext.current
    val weightStore = remember(context) { WeightStore(context) }
    val profileStore = remember(context) { ProfileStore(context) }
    val entries = remember { mutableStateListOf<WeightEntry>().apply { addAll(weightStore.load()) } }
    var profile by remember { mutableStateOf(profileStore.load()) }
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.WEIGHT) }
    var selectedWeightPage by rememberSaveable { mutableStateOf(WeightPage.DASHBOARD) }
    var selectedWeightDateEpochDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    var weightInput by rememberSaveable { mutableStateOf("") }
    var weightErrorText by rememberSaveable { mutableStateOf<String?>(null) }
    var heightInput by rememberSaveable { mutableStateOf(profile.heightInCm?.let(::formatNumber).orEmpty()) }
    var heightErrorText by rememberSaveable { mutableStateOf<String?>(null) }
    var targetWeightInput by rememberSaveable {
        mutableStateOf(profile.targetWeightInKg?.let(::formatNumber).orEmpty())
    }
    var targetWeightErrorText by rememberSaveable { mutableStateOf<String?>(null) }

    val latestEntry = entries.firstOrNull()
    val previousEntry = entries.getOrNull(1)
    val trend = latestEntry?.let { latest ->
        previousEntry?.let { previous -> latest.weightInKg - previous.weightInKg }
    }
    val bmi = calculateBmi(latestEntry?.weightInKg, profile.heightInCm)
    val bmiCategory = classifyBmi(bmi)
    val healthyWeightRange = calculateHealthyWeightRange(profile.heightInCm)
    val optimalWeight = calculateOptimalWeight(profile.heightInCm)

    FitnessBoyScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        selectedWeightPage = selectedWeightPage,
        onWeightPageChange = { selectedWeightPage = it },
        entries = entries,
        profile = profile,
        latestEntry = latestEntry,
        trend = trend,
        bmi = bmi,
        bmiCategory = bmiCategory,
        healthyWeightRangeText = healthyWeightRange?.let {
            "${formatNumber(it.minimumInKg)} bis ${formatNumber(it.maximumInKg)} kg"
        },
        optimalWeightText = optimalWeight?.let { "${formatNumber(it)} kg" },
        weightInput = weightInput,
        selectedWeightDate = LocalDate.ofEpochDay(selectedWeightDateEpochDay),
        weightErrorText = weightErrorText,
        heightInput = heightInput,
        heightErrorText = heightErrorText,
        targetWeightInput = targetWeightInput,
        targetWeightErrorText = targetWeightErrorText,
        onWeightValueChange = {
            weightInput = it
            weightErrorText = null
        },
        onSelectedWeightDateChange = {
            selectedWeightDateEpochDay = it.toEpochDay()
        },
        onHeightValueChange = {
            heightInput = it
            heightErrorText = null
        },
        onTargetWeightValueChange = {
            targetWeightInput = it
            targetWeightErrorText = null
        },
        onSaveProfileClick = {
            val parsedHeight = parseHeight(heightInput)
            if (parsedHeight == null) {
                heightErrorText = "Bitte eine gültige Größe in cm eingeben."
                return@FitnessBoyScreen
            }

            val parsedTargetWeight = parseOptionalWeight(targetWeightInput)
            if (targetWeightInput.isNotBlank() && parsedTargetWeight == null) {
                targetWeightErrorText = "Bitte ein gültiges Zielgewicht eingeben."
                return@FitnessBoyScreen
            }

            val updatedProfile = profile.copy(
                heightInCm = parsedHeight,
                targetWeightInKg = parsedTargetWeight,
            )
            profile = updatedProfile
            profileStore.save(updatedProfile)
            heightInput = formatNumber(parsedHeight)
            targetWeightInput = parsedTargetWeight?.let(::formatNumber).orEmpty()
        },
        onAddWeightClick = {
            val parsedWeight = parseWeight(weightInput)
            if (parsedWeight == null) {
                weightErrorText = "Bitte ein gültiges Gewicht eingeben."
                return@FitnessBoyScreen
            }

            val updatedEntries = (entries + WeightEntry(LocalDate.ofEpochDay(selectedWeightDateEpochDay), parsedWeight))
                .sortedByDescending(WeightEntry::date)
            entries.clear()
            entries.addAll(updatedEntries)
            weightStore.save(entries)
            weightInput = ""
            selectedWeightDateEpochDay = LocalDate.now().toEpochDay()
        },
        onDeleteEntry = { entry ->
            entries.remove(entry)
            weightStore.save(entries)
        },
    )
}

@Composable
private fun FitnessBoyScreen(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    selectedWeightPage: WeightPage,
    onWeightPageChange: (WeightPage) -> Unit,
    entries: List<WeightEntry>,
    profile: UserProfile,
    latestEntry: WeightEntry?,
    trend: Double?,
    bmi: Double?,
    bmiCategory: String?,
    healthyWeightRangeText: String?,
    optimalWeightText: String?,
    weightInput: String,
    selectedWeightDate: LocalDate,
    weightErrorText: String?,
    heightInput: String,
    heightErrorText: String?,
    targetWeightInput: String,
    targetWeightErrorText: String?,
    onWeightValueChange: (String) -> Unit,
    onSelectedWeightDateChange: (LocalDate) -> Unit,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveProfileClick: () -> Unit,
    onAddWeightClick: () -> Unit,
    onDeleteEntry: (WeightEntry) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                tonalElevation = 0.dp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            ) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.WEIGHT -> WeightTab(
                    selectedWeightPage = selectedWeightPage,
                    onWeightPageChange = onWeightPageChange,
                    entries = entries,
                    targetWeightInKg = profile.targetWeightInKg,
                    latestEntry = latestEntry,
                    trend = trend,
                    bmi = bmi,
                    weightInput = weightInput,
                    selectedWeightDate = selectedWeightDate,
                    weightErrorText = weightErrorText,
                    onWeightValueChange = onWeightValueChange,
                    onSelectedWeightDateChange = onSelectedWeightDateChange,
                    onAddWeightClick = onAddWeightClick,
                    onDeleteEntry = onDeleteEntry,
                )

                AppTab.BMI -> BmiTab(
                    latestEntry = latestEntry,
                    bmi = bmi,
                    bmiCategory = bmiCategory,
                    optimalWeightText = optimalWeightText,
                    healthyWeightRangeText = healthyWeightRangeText,
                    heightInCm = profile.heightInCm,
                )

                AppTab.PROFILE -> ProfileTab(
                    profile = profile,
                    bmi = bmi,
                    heightInput = heightInput,
                    heightErrorText = heightErrorText,
                    targetWeightInput = targetWeightInput,
                    targetWeightErrorText = targetWeightErrorText,
                    onHeightValueChange = onHeightValueChange,
                    onTargetWeightValueChange = onTargetWeightValueChange,
                    onSaveProfileClick = onSaveProfileClick,
                )
            }
        }
    }
}

@Composable
private fun WeightTab(
    selectedWeightPage: WeightPage,
    onWeightPageChange: (WeightPage) -> Unit,
    entries: List<WeightEntry>,
    targetWeightInKg: Double?,
    latestEntry: WeightEntry?,
    trend: Double?,
    bmi: Double?,
    weightInput: String,
    selectedWeightDate: LocalDate,
    weightErrorText: String?,
    onWeightValueChange: (String) -> Unit,
    onSelectedWeightDateChange: (LocalDate) -> Unit,
    onAddWeightClick: () -> Unit,
    onDeleteEntry: (WeightEntry) -> Unit,
) {
    if (selectedWeightPage == WeightPage.HISTORY) {
        WeightHistoryPage(
            entries = entries,
            onBackClick = { onWeightPageChange(WeightPage.DASHBOARD) },
            onDeleteEntry = onDeleteEntry,
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Gewicht",
                subtitle = "Trage neue Werte ein und beobachte deinen Verlauf.",
                latestEntry = latestEntry,
                trend = trend,
                bmi = bmi,
            )
        }

        item {
            AddWeightCard(
                value = weightInput,
                selectedDate = selectedWeightDate,
                errorText = weightErrorText,
                onValueChange = onWeightValueChange,
                onDateChange = onSelectedWeightDateChange,
                onAddClick = onAddWeightClick,
            )
        }

        item {
            if (entries.isEmpty()) {
                EmptyStateCard()
            } else {
                WeightChartCard(
                    entries = entries,
                    targetWeightInKg = targetWeightInKg,
                )
            }
        }

        if (!entries.isEmpty()) {
            item {
                OutlinedButton(
                    onClick = { onWeightPageChange(WeightPage.HISTORY) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                )
                {
                    Text("Verlauf öffnen")
                }
            }
        }
    }
}

@Composable
private fun WeightHistoryPage(
    entries: List<WeightEntry>,
    onBackClick: () -> Unit,
    onDeleteEntry: (WeightEntry) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Verlauf",
                subtitle = "Hier findest du alle gespeicherten Wiegeeinträge und kannst sie bei Bedarf löschen.",
                latestEntry = entries.firstOrNull(),
                trend = null,
                bmi = null,
            )
        }

        item {
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Zurück zur Gewichtsseite")
            }
        }

        items(entries, key = { "${it.date}-${it.weightInKg}" }) { entry ->
            WeightHistoryRow(
                entry = entry,
                onDelete = { onDeleteEntry(entry) },
            )
        }
    }
}

@Composable
private fun BmiTab(
    latestEntry: WeightEntry?,
    bmi: Double?,
    bmiCategory: String?,
    optimalWeightText: String?,
    healthyWeightRangeText: String?,
    heightInCm: Double?,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "BMI und Ziele",
                subtitle = "Hier siehst du deinen BMI und Richtwerte auf Basis deiner Größe.",
                latestEntry = latestEntry,
                trend = null,
                bmi = bmi,
            )
        }

        item {
            BmiInsightsCard(
                bmi = bmi?.let(::formatNumber),
                bmiCategory = bmiCategory,
                heightText = heightInCm?.let { "${formatNumber(it)} cm" },
                currentWeightText = latestEntry?.let { "${formatNumber(it.weightInKg)} kg" },
            )
        }

        item {
            MetricInfoCard(
                title = "Optimalgewicht",
                value = optimalWeightText ?: "Bitte Profil ausfüllen",
                description = "Grobe Orientierung mit BMI 22 als Zielwert.",
            )
        }

        item {
            MetricInfoCard(
                title = "Gesunder Bereich",
                value = healthyWeightRangeText ?: "Bitte Profil ausfüllen",
                description = "Abgeleitet aus BMI 18.5 bis 24.9 für deine Größe.",
            )
        }
    }
}

@Composable
private fun ProfileTab(
    profile: UserProfile,
    bmi: Double?,
    heightInput: String,
    heightErrorText: String?,
    targetWeightInput: String,
    targetWeightErrorText: String?,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveProfileClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Profil",
                subtitle = "Passe hier deine Körperdaten und Zielwerte an.",
                latestEntry = null,
                trend = null,
                bmi = bmi,
            )
        }

        item {
            ProfileCard(
                heightValue = heightInput,
                targetWeightValue = targetWeightInput,
                savedHeightInCm = profile.heightInCm,
                savedTargetWeightInKg = profile.targetWeightInKg,
                heightErrorText = heightErrorText,
                targetWeightErrorText = targetWeightErrorText,
                bmi = bmi,
                onHeightValueChange = onHeightValueChange,
                onTargetWeightValueChange = onTargetWeightValueChange,
                onSaveClick = onSaveProfileClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FitnessBoyAppPreview() {
    FitnessBoyTheme {
        FitnessBoyScreen(
            selectedTab = AppTab.WEIGHT,
            onTabSelected = {},
            selectedWeightPage = WeightPage.DASHBOARD,
            onWeightPageChange = {},
            entries = listOf(
                WeightEntry(LocalDate.of(2026, 3, 8), 82.9),
                WeightEntry(LocalDate.of(2026, 3, 9), 82.4),
            ),
            profile = UserProfile(heightInCm = 180.0, targetWeightInKg = 78.0),
            latestEntry = WeightEntry(LocalDate.of(2026, 3, 9), 82.4),
            trend = -0.5,
            bmi = 25.4,
            bmiCategory = "Übergewicht",
            healthyWeightRangeText = "59.9 bis 80.7 kg",
            optimalWeightText = "71.3 kg",
            weightInput = "",
            selectedWeightDate = LocalDate.of(2026, 3, 9),
            weightErrorText = null,
            heightInput = "180",
            heightErrorText = null,
            targetWeightInput = "78",
            targetWeightErrorText = null,
            onWeightValueChange = {},
            onSelectedWeightDateChange = {},
            onHeightValueChange = {},
            onTargetWeightValueChange = {},
            onSaveProfileClick = {},
            onAddWeightClick = {},
            onDeleteEntry = {},
        )
    }
}
