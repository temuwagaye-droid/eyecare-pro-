package com.example.ui.risk

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LanguageOption(val code: String, val name: String, val flag: String)

val supportedLanguages = listOf(
    LanguageOption("en", "English", "🇺🇸"),
    LanguageOption("es", "Español", "🇪🇸"),
    LanguageOption("fr", "Français", "🇫🇷"),
    LanguageOption("de", "Deutsch", "🇩🇪"),
    LanguageOption("zh", "中文", "🇨🇳"),
    LanguageOption("ar", "العربية", "🇸🇦"),
    LanguageOption("sw", "Kiswahili", "🇰🇪"),
    LanguageOption("pt", "Português", "🇧🇷")
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RiskPredictionScreen(
    onBack: () -> Unit
) {
    var selectedLang by remember { mutableStateOf("en") }
    var age by remember { mutableFloatStateOf(28f) }
    var screenHours by remember { mutableFloatStateOf(6f) }

    // Symptoms map
    var symptomFatigue by remember { mutableStateOf(true) }
    var symptomDryEyes by remember { mutableStateOf(false) }
    var symptomBlurred by remember { mutableStateOf(false) }
    var symptomHeadache by remember { mutableStateOf(false) }
    var symptomNeck by remember { mutableStateOf(false) }
    var symptomLightSensitivity by remember { mutableStateOf(false) }
    var symptomFocusLag by remember { mutableStateOf(false) }

    var calculatedResult by remember { mutableStateOf<RiskResult?>(null) }

    // Translations dictionary
    val t = getTranslations(selectedLang)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t["title"] ?: "Eye Strain Risk Prediction") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t["back"] ?: "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(t["lang_title"] ?: "Select Language / Idioma / Langue", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    
                    // Language Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        supportedLanguages.forEach { lang ->
                            FilterChip(
                                selected = selectedLang == lang.code,
                                onClick = { selectedLang = lang.code },
                                label = { Text("${lang.flag} ${lang.name}") },
                                modifier = Modifier.testTag("lang_chip_${lang.code}")
                            )
                        }
                    }
                }
            }

            // Input Parameters Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(t["params_header"] ?: "Personal Factors & Usage", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)

                    // Age Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(t["age_label"] ?: "Age", fontWeight = FontWeight.Medium)
                            Text("${age.toInt()} ${t["years"] ?: "years"}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = age,
                            onValueChange = { age = it },
                            valueRange = 5f..95f,
                            steps = 90,
                            modifier = Modifier.testTag("age_slider")
                        )
                    }

                    // Screen Time Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(t["screentime_label"] ?: "Daily Screen Time", fontWeight = FontWeight.Medium)
                            Text(String.format("%.1f %s", screenHours, t["hours"] ?: "hrs/day"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = screenHours,
                            onValueChange = { screenHours = it },
                            valueRange = 1f..16f,
                            steps = 30,
                            modifier = Modifier.testTag("screentime_slider")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Symptoms Checkboxes
                    Text(t["symptoms_header"] ?: "Select Current Symptoms", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SymptomRow(
                            label = t["sym_fatigue"] ?: "Eye Fatigue / Tired Eyes",
                            checked = symptomFatigue,
                            onCheckedChange = { symptomFatigue = it },
                            testTag = "sym_fatigue_checkbox"
                        )
                        SymptomRow(
                            label = t["sym_dry"] ?: "Dry or Burning Eyes",
                            checked = symptomDryEyes,
                            onCheckedChange = { symptomDryEyes = it },
                            testTag = "sym_dry_checkbox"
                        )
                        SymptomRow(
                            label = t["sym_blurred"] ?: "Blurred Vision",
                            checked = symptomBlurred,
                            onCheckedChange = { symptomBlurred = it },
                            testTag = "sym_blurred_checkbox"
                        )
                        SymptomRow(
                            label = t["sym_headache"] ?: "Headaches after screen use",
                            checked = symptomHeadache,
                            onCheckedChange = { symptomHeadache = it },
                            testTag = "sym_headache_checkbox"
                        )
                        SymptomRow(
                            label = t["sym_neck"] ?: "Neck / Shoulder Strain",
                            checked = symptomNeck,
                            onCheckedChange = { symptomNeck = it },
                            testTag = "sym_neck_checkbox"
                        )
                        SymptomRow(
                            label = t["sym_light"] ?: "Sensitivity to Light",
                            checked = symptomLightSensitivity,
                            onCheckedChange = { symptomLightSensitivity = it },
                            testTag = "sym_light_checkbox"
                        )
                        SymptomRow(
                            label = t["sym_focus"] ?: "Difficulty focusing after screens",
                            checked = symptomFocusLag,
                            onCheckedChange = { symptomFocusLag = it },
                            testTag = "sym_focus_checkbox"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            calculatedResult = calculateRisk(
                                age = age,
                                screenHours = screenHours,
                                symptomsCount = listOf(symptomFatigue, symptomDryEyes, symptomBlurred, symptomHeadache, symptomNeck, symptomLightSensitivity, symptomFocusLag).count { it },
                                lang = selectedLang
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("calculate_risk_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t["calculate_btn"] ?: "Predict Digital Eye Strain Risk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Results Section
            AnimatedVisibility(
                visible = calculatedResult != null,
                enter = fadeIn() + expandVertically()
            ) {
                calculatedResult?.let { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("risk_result_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (result.level) {
                                RiskLevel.LOW -> MaterialTheme.colorScheme.primaryContainer
                                RiskLevel.MODERATE -> MaterialTheme.colorScheme.tertiaryContainer
                                RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
                                RiskLevel.CRITICAL -> MaterialTheme.colorScheme.error
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t["report_title"] ?: "Risk Prediction Report",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        text = "${result.score}% Risk Score",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Text(
                                text = result.levelTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = result.description,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

                            Text(
                                text = t["recommendations"] ?: "Tailored Recommendations:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )

                            result.recommendations.forEach { rec ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text(text = rec, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SymptomRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, testTag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}

enum class RiskLevel { LOW, MODERATE, HIGH, CRITICAL }

data class RiskResult(
    val score: Int,
    val level: RiskLevel,
    val levelTitle: String,
    val description: String,
    val recommendations: List<String>
)

fun calculateRisk(age: Float, screenHours: Float, symptomsCount: Int, lang: String): RiskResult {
    // Scoring formula: base score from screen hours (up to 40), age factor (older/younger extremes add fatigue), symptoms (10 pts each)
    var score = (screenHours * 4.5f).toInt() + (symptomsCount * 12)
    if (age < 18 || age > 60) score += 10
    if (score > 100) score = 100
    if (score < 5) score = 5

    val level = when {
        score < 30 -> RiskLevel.LOW
        score < 60 -> RiskLevel.MODERATE
        score < 85 -> RiskLevel.HIGH
        else -> RiskLevel.CRITICAL
    }

    val t = getTranslations(lang)

    val levelTitle = when (level) {
        RiskLevel.LOW -> t["risk_low"] ?: "Low Eye Strain Risk"
        RiskLevel.MODERATE -> t["risk_mod"] ?: "Moderate Eye Strain Risk"
        RiskLevel.HIGH -> t["risk_high"] ?: "High Eye Strain Risk"
        RiskLevel.CRITICAL -> t["risk_crit"] ?: "Critical Eye Fatigue Risk"
    }

    val description = when (level) {
        RiskLevel.LOW -> t["desc_low"] ?: "Your current screen time and symptoms indicate healthy eye habits. Keep maintaining regular breaks."
        RiskLevel.MODERATE -> t["desc_mod"] ?: "You are showing signs of moderate digital eye strain. Applying the 20-20-20 rule will significantly help."
        RiskLevel.HIGH -> t["desc_high"] ?: "High risk of computer vision syndrome detected based on your high screen exposure and reported symptoms."
        RiskLevel.CRITICAL -> t["desc_crit"] ?: "Critical strain level! Immediate screen break, blue light filtering, and ergonomic adjustments are strongly recommended."
    }

    val recommendations = listOf(
        t["rec_1"] ?: "Practice the 20-20-20 rule: Every 20 minutes, look 20 feet away for 20 seconds.",
        t["rec_2"] ?: "Adjust screen brightness to match your ambient lighting and enable Blue Light Shield.",
        t["rec_3"] ?: "Blink frequently and use lubricating eye drops to prevent dryness."
    )

    return RiskResult(score, level, levelTitle, description, recommendations)
}

fun getTranslations(lang: String): Map<String, String> {
    return when (lang) {
        "es" -> mapOf(
            "title" to "Predicción de Riesgo Visual",
            "back" to "Volver",
            "lang_title" to "Seleccionar Idioma",
            "params_header" to "Factores Personales y Uso",
            "age_label" to "Edad",
            "years" to "años",
            "screentime_label" to "Tiempo de Pantalla Diario",
            "hours" to "hrs/día",
            "symptoms_header" to "Síntomas Actuales",
            "sym_fatigue" to "Fatiga o Cansancio Ocular",
            "sym_dry" to "Ojos Secos o Ardor",
            "sym_blurred" to "Visión Borrosa",
            "sym_headache" to "Dolores de Cabeza",
            "sym_neck" to "Tensión en Cuello / Hombros",
            "sym_light" to "Sensibilidad a la Luz",
            "sym_focus" to "Dificultad para Enfocar",
            "calculate_btn" to "Predecir Riesgo de Fatiga Ocular",
            "report_title" to "Informe de Predicción de Riesgo",
            "recommendations" to "Recomendaciones:",
            "risk_low" to "Riesgo Bajo de Fatiga Ocular",
            "risk_mod" to "Riesgo Moderado de Fatiga Ocular",
            "risk_high" to "Riesgo Alto de Fatiga Ocular",
            "risk_crit" to "¡Riesgo Crítico de Fatiga Ocular!",
            "desc_low" to "Tus hábitos actuales son saludables. Sigue tomando descansos regulares.",
            "desc_mod" to "Muestras signos moderados de fatiga visual digital. La regla 20-20-20 te ayudará.",
            "desc_high" to "Alto riesgo de síndrome visual informático detectado según tu uso y síntomas.",
            "desc_crit" to "¡Nivel de fatiga crítico! Se recomienda un descanso inmediato y filtro de luz azul."
        )
        "fr" -> mapOf(
            "title" to "Prédiction du Risque Visuel",
            "back" to "Retour",
            "lang_title" to "Sélectionner la Langue",
            "params_header" to "Facteurs Personnels & Utilisation",
            "age_label" to "Âge",
            "years" to "ans",
            "screentime_label" to "Temps d'Écran Quotidien",
            "hours" to "h/jour",
            "symptoms_header" to "Symptômes Actuels",
            "sym_fatigue" to "Fatigue Oculaire",
            "sym_dry" to "Yeux Secs ou Picotements",
            "sym_blurred" to "Vision Floue",
            "sym_headache" to "Maux de Tête",
            "sym_neck" to "Tension au Cou / Épaules",
            "sym_light" to "Sensibilité à la Lumière",
            "sym_focus" to "Difficulté à Focaliser",
            "calculate_btn" to "Prédire le Risque de Fatigue Oculaire",
            "report_title" to "Rapport de Prédiction",
            "recommendations" to "Recommandations :",
            "risk_low" to "Faible Risque de Fatigue Oculaire",
            "risk_mod" to "Risque Modéré de Fatigue Oculaire",
            "risk_high" to "Risque Élevé de Fatigue Oculaire",
            "risk_crit" to "Risque Critique de Fatigue Oculaire !",
            "desc_low" to "Vos habitudes d'écran sont saines. Continuez ainsi.",
            "desc_mod" to "Signes modérés de fatigue numérique détectés. La règle 20-20-20 vous aidera.",
            "desc_high" to "Risque élevé de syndrome visuel informatique détecté.",
            "desc_crit" to "Niveau de fatigue critique ! Pause écran et filtre anti-lumière bleue recommandés."
        )
        "de" -> mapOf(
            "title" to "Augenbelastungs-Risikovorhersage",
            "back" to "Zurück",
            "lang_title" to "Sprache Auswählen",
            "params_header" to "Persönliche Faktoren & Nutzung",
            "age_label" to "Alter",
            "years" to "Jahre",
            "screentime_label" to "Tägliche Bildschirmzeit",
            "hours" to "Std/Tag",
            "symptoms_header" to "Aktuelle Symptome",
            "sym_fatigue" to "Müde Augen / Überlastung",
            "sym_dry" to "Trockene oder brennende Augen",
            "sym_blurred" to "Verschwommene Sicht",
            "sym_headache" to "Kopfschmerzen",
            "sym_neck" to "Nacken- / Schulterverspannung",
            "sym_light" to "Lichtempfindlichkeit",
            "sym_focus" to "Fokussierungsschwierigkeiten",
            "calculate_btn" to "Risiko Vorhersagen",
            "report_title" to "Risikobericht",
            "recommendations" to "Empfehlungen:",
            "risk_low" to "Geringes Risiko",
            "risk_mod" to "Mittleres Risiko",
            "risk_high" to "Hohes Risiko",
            "risk_crit" to "Kritisches Risiko!",
            "desc_low" to "Ihre Bildschirmgewohnheiten sind gesund.",
            "desc_mod" to "Moderate Anzeichen von digitaler Augenbelastung.",
            "desc_high" to "Hohes Risiko für Computer-Vision-Syndrom.",
            "desc_crit" to "Kritisches Belastungsniveau! Sofortige Bildschirmpause empfohlen."
        )
        "zh" -> mapOf(
            "title" to "视疲劳风险预测系统",
            "back" to "返回",
            "lang_title" to "选择语言",
            "params_header" to "个人因素与使用情况",
            "age_label" to "年龄",
            "years" to "岁",
            "screentime_label" to "每日屏幕时间",
            "hours" to "小时/天",
            "symptoms_header" to "当前症状",
            "sym_fatigue" to "眼睛疲劳/酸胀",
            "sym_dry" to "干眼症/烧灼感",
            "sym_blurred" to "视力模糊",
            "sym_headache" to "头痛",
            "sym_neck" to "颈部/肩膀酸痛",
            "sym_light" to "畏光/对光敏感",
            "sym_focus" to "对焦困难",
            "calculate_btn" to "预测视疲劳风险",
            "report_title" to "风险预测报告",
            "recommendations" to "专家建议：",
            "risk_low" to "低视疲劳风险",
            "risk_mod" to "中度视疲劳风险",
            "risk_high" to "高度视疲劳风险",
            "risk_crit" to "极高/危险视疲劳等级！",
            "desc_low" to "您的屏幕使用习惯良好，请保持定时休息。",
            "desc_mod" to "您出现中度数码视疲劳迹象，建议应用20-20-20护眼法则。",
            "desc_high" to "检测到高风险计算机视觉综合症。",
            "desc_crit" to "严重视疲劳警报！强烈建议立即休息并启用防蓝光模式。"
        )
        "ar" -> mapOf(
            "title" to "نظام التنبؤ بمخاطر إجهاد العين",
            "back" to "رجوع",
            "lang_title" to "اختر اللغة",
            "params_header" to "العوامل الشخصية والاستخدام",
            "age_label" to "العمر",
            "years" to "سنوات",
            "screentime_label" to "وقت الشاشة اليومي",
            "hours" to "ساعات/يوم",
            "symptoms_header" to "الأعراض الحالية",
            "sym_fatigue" to "إجهاد العين",
            "sym_dry" to "جفاف أو حرقة العيون",
            "sym_blurred" to "رؤية ضبابية",
            "sym_headache" to "صداع",
            "sym_neck" to "إجهاد الرقبة / الكتفين",
            "sym_light" to "حساسية الضوء",
            "sym_focus" to "صعوبة التركيز",
            "calculate_btn" to "التنبؤ بمخاطر إجهاد العين",
            "report_title" to "تقرير التنبؤ بالمخاطر",
            "recommendations" to "التوصيات المقترحة:",
            "risk_low" to "خطر إجهاد منخفض",
            "risk_mod" to "خطر إجهاد متوسط",
            "risk_high" to "خطر إجهاد مرتفع",
            "risk_crit" to "خطر حرج لإجهاد العين!",
            "desc_low" to "عادات استخدام الشاشة لديك صحية. حافظ على الاستراحات المنتظمة.",
            "desc_mod" to "تظهر علامات متوسطة لإجهاد العين الرقمي. قاعدة 20-20-20 ستساعدك كثيراً.",
            "desc_high" to "تم اكتشاف خطر عالٍ لمتلازمة رؤية الكمبيوتر بناءً على الاستخدام والأعراض.",
            "desc_crit" to "مستوى إجهاد حرج! يوصى بشدة بأخذ قسط من الراحة وتفعيل فلتر الضوء الأزرق."
        )
        "sw" -> mapOf(
            "title" to "Mfumo wa Utabiri wa Hatari ya Macho",
            "back" to "Rudi",
            "lang_title" to "Chagua Lugha",
            "params_header" to "Vigezo vya Binafsi na Matumizi",
            "age_label" to "Umri",
            "years" to "miaka",
            "screentime_label" to "Muda wa Skrini Kila Siku",
            "hours" to "saa/siku",
            "symptoms_header" to "Dalili za Sasa",
            "sym_fatigue" to "Uchovu wa Macho",
            "sym_dry" to "Macho Makavu au Kuwasha",
            "sym_blurred" to "Kutokuona Vizuri (Blurry)",
            "sym_headache" to "Maumivu ya Kichwa",
            "sym_neck" to "Maumivu ya Shingo / Mabega",
            "sym_light" to "Hali ya Kutovumilia Mwanga",
            "sym_focus" to "Ugumu wa Kulenga Macho",
            "calculate_btn" to "Tabiri Hatari ya Uchovu wa Macho",
            "report_title" to "Ripoti ya Utabiri wa Hatari",
            "recommendations" to "Mapendekezo:",
            "risk_low" to "Hatari Ndogo ya Macho",
            "risk_mod" to "Hatari ya Wastani",
            "risk_high" to "Hatari Kubwa ya Uchovu",
            "risk_crit" to "Hatari Kubwa Sana!",
            "desc_low" to "Tabia zako za skrini ni nzuri. Endelea kupumzika mara kwa mara.",
            "desc_mod" to "Unaonyesha dalili za wastani za uchovu wa macho. Sheria ya 20-20-20 itasaidia.",
            "desc_high" to "Hatari kubwa ya ugonjwa wa macho wa kompyuta imegunduliwa.",
            "desc_crit" to "Kiwango hatari cha uchovu! Pumzika mara moja na utumie kichungi cha mwangaza wa bluu."
        )
        "pt" -> mapOf(
            "title" to "Sistema de Previsão de Risco Ocular",
            "back" to "Voltar",
            "lang_title" to "Selecionar Idioma",
            "params_header" to "Fatores Pessoais e Uso",
            "age_label" to "Idade",
            "years" to "anos",
            "screentime_label" to "Tempo de Tela Diário",
            "hours" to "hrs/dia",
            "symptoms_header" to "Sintomas Atuais",
            "sym_fatigue" to "Fadiga ou Cansaço Visual",
            "sym_dry" to "Olhos Secos ou Ardor",
            "sym_blurred" to "Visão Embaçada",
            "sym_headache" to "Dores de Cabeça",
            "sym_neck" to "Tensão no Pescoço / Ombros",
            "sym_light" to "Sensibilidade à Luz",
            "sym_focus" to "Dificuldade de Foco",
            "calculate_btn" to "Prever Risco de Fadiga Ocular",
            "report_title" to "Relatório de Previsão de Risco",
            "recommendations" to "Recomendações:",
            "risk_low" to "Baixo Risco de Fadiga Ocular",
            "risk_mod" to "Risco Moderado de Fadiga Ocular",
            "risk_high" to "Alto Risco de Fadiga Ocular",
            "risk_crit" to "Risco Crítico de Fadiga Ocular!",
            "desc_low" to "Seus hábitos de tela estão saudáveis. Continue fazendo pausas regulares.",
            "desc_mod" to "Você apresenta sinais moderados de fadiga digital. A regra 20-20-20 ajudará muito.",
            "desc_high" to "Alto risco de síndrome visual de computador detectado com base no uso e sintomas.",
            "desc_crit" to "Nível de fadiga crítico! Pausa imediata, filtro de luz azul e ajustes ergonômicos recomendados."
        )
        else -> mapOf(
            "title" to "Eye Strain Risk Prediction",
            "back" to "Back",
            "lang_title" to "Select Language",
            "params_header" to "Personal Factors & Usage",
            "age_label" to "Age",
            "years" to "years",
            "screentime_label" to "Daily Screen Time",
            "hours" to "hrs/day",
            "symptoms_header" to "Select Current Symptoms",
            "sym_fatigue" to "Eye Fatigue / Tired Eyes",
            "sym_dry" to "Dry or Burning Eyes",
            "sym_blurred" to "Blurred Vision",
            "sym_headache" to "Headaches after screen use",
            "sym_neck" to "Neck / Shoulder Strain",
            "sym_light" to "Sensitivity to Light",
            "sym_focus" to "Difficulty focusing after screens",
            "calculate_btn" to "Predict Digital Eye Strain Risk",
            "report_title" to "Risk Prediction Report",
            "recommendations" to "Tailored Recommendations:",
            "risk_low" to "Low Eye Strain Risk",
            "risk_mod" to "Moderate Eye Strain Risk",
            "risk_high" to "High Eye Strain Risk",
            "risk_crit" to "Critical Eye Fatigue Risk",
            "desc_low" to "Your current screen time and symptoms indicate healthy eye habits. Keep maintaining regular breaks.",
            "desc_mod" to "You are showing signs of moderate digital eye strain. Applying the 20-20-20 rule will significantly help.",
            "desc_high" to "High risk of computer vision syndrome detected based on your high screen exposure and reported symptoms.",
            "desc_crit" to "Critical strain level! Immediate screen break, blue light filtering, and ergonomic adjustments are strongly recommended."
        )
    }
}
