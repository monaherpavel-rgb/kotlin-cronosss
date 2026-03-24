package com.cronos.app.ui.screens.inspector

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cronos.app.data.stub.STUB_PARTICIPANTS
import com.cronos.app.data.stub.StubParticipant

private fun stripMarkdown(text: String): String =
    text.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
        .replace(Regex("\\*(.+?)\\*"), "$1")
        .replace(Regex("#{1,6}\\s"), "")
        .replace(Regex("`(.+?)`"), "$1")
        .trim()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectorScreen(navController: NavController, viewModel: InspectorViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var showFilters by remember { mutableStateOf(false) }
    var filterCity by remember { mutableStateOf("") }
    var filterDirection by remember { mutableStateOf("") }
    var filterLevel by remember { mutableStateOf("") }
    var filterMinEvents by remember { mutableStateOf("") }
    var filterMinRating by remember { mutableStateOf("") }
    var showCompareDialog by remember { mutableStateOf(false) }

    val directions = listOf("", "IT", "Медиа", "Социальные проекты", "Политика", "Наука", "Бизнес")
    val levels = listOf("", "Bronze", "Silver", "Gold", "Reserve")

    val filtered = STUB_PARTICIPANTS.filter { p ->
        (filterCity.isBlank() || p.city.contains(filterCity, ignoreCase = true)) &&
        (filterDirection.isBlank() || p.direction == filterDirection) &&
        (filterLevel.isBlank() || p.level == filterLevel) &&
        (filterMinEvents.isBlank() || p.eventsCount >= (filterMinEvents.toIntOrNull() ?: 0)) &&
        (filterMinRating.isBlank() || p.rating >= (filterMinRating.toIntOrNull() ?: 0))
    }.sortedByDescending { it.rating }

    val compareState by viewModel.compareState.collectAsState()

    // Таймер обновления резерва каждые 12 часов
    val totalSeconds = 12 * 3600
    var secondsLeft by remember { mutableStateOf(totalSeconds) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            secondsLeft = if (secondsLeft > 0) secondsLeft - 1 else totalSeconds
        }
    }
    val hoursLeft = secondsLeft / 3600
    val minutesLeft = (secondsLeft % 3600) / 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Инспектор резерва") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showCompareDialog = true }) {
                        Icon(Icons.Default.CompareArrows, "Сравнить")
                    }
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList, "Фильтры")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showFilters) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Фильтры", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(value = filterCity, onValueChange = { filterCity = it },
                            label = { Text("Город") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = filterMinEvents, onValueChange = { filterMinEvents = it },
                            label = { Text("Мин. мероприятий") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = filterMinRating, onValueChange = { filterMinRating = it },
                            label = { Text("Мин. рейтинг") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Text("Направление", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            directions.forEach { dir ->
                                FilterChip(selected = filterDirection == dir, onClick = { filterDirection = dir },
                                    label = { Text(if (dir.isBlank()) "Все" else dir) })
                            }
                        }
                        Text("Уровень", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            levels.forEach { lvl ->
                                FilterChip(selected = filterLevel == lvl, onClick = { filterLevel = lvl },
                                    label = { Text(if (lvl.isBlank()) "Все" else lvl) })
                            }
                        }
                        TextButton(onClick = {
                            filterCity = ""; filterDirection = ""; filterLevel = ""
                            filterMinEvents = ""; filterMinRating = ""
                        }) { Text("Сбросить фильтры") }
                    }
                }
            }

            Text(
                "Найдено: ${filtered.size} кандидатов",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Schedule, null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Обновление резерва через ${hoursLeft}ч ${minutesLeft}мин",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { candidate -> CandidateCard(candidate, viewModel) }
                if (filtered.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Нет кандидатов по фильтрам", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Диалог AI-сравнения
    if (showCompareDialog) {
        var candidateA by remember { mutableStateOf(STUB_PARTICIPANTS.first()) }
        var candidateB by remember { mutableStateOf(STUB_PARTICIPANTS[1]) }
        AlertDialog(
            onDismissRequest = { showCompareDialog = false },
            title = { Text("AI-сравнение кандидатов") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Выберите двух кандидатов:", style = MaterialTheme.typography.bodySmall)
                    var expandA by remember { mutableStateOf(false) }
                    var expandB by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandA, onExpandedChange = { expandA = it }) {
                        OutlinedTextField(
                            value = candidateA.name, onValueChange = {}, readOnly = true,
                            label = { Text("Кандидат А") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandA) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandA, onDismissRequest = { expandA = false }) {
                            STUB_PARTICIPANTS.forEach { p ->
                                DropdownMenuItem(text = { Text(p.name) }, onClick = { candidateA = p; expandA = false })
                            }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = expandB, onExpandedChange = { expandB = it }) {
                        OutlinedTextField(
                            value = candidateB.name, onValueChange = {}, readOnly = true,
                            label = { Text("Кандидат Б") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandB) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandB, onDismissRequest = { expandB = false }) {
                            STUB_PARTICIPANTS.forEach { p ->
                                DropdownMenuItem(text = { Text(p.name) }, onClick = { candidateB = p; expandB = false })
                            }
                        }
                    }
                    if (compareState.isLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            Text("AI анализирует кандидатов...", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (compareState.result != null) {
                        // Текст не показываем — только кнопка скачать PDF
                        OutlinedButton(
                            onClick = { exportComparePdf(context, candidateA, candidateB, compareState.result!!) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Скачать сравнение PDF")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.compareCandidates(candidateA, candidateB) },
                    enabled = !compareState.isLoading && candidateA.id != candidateB.id
                ) { Text("Сравнить AI") }
            },
            dismissButton = {
                TextButton(onClick = { showCompareDialog = false }) { Text("Закрыть") }
            }
        )
    }
}

@Composable
private fun CandidateCard(candidate: StubParticipant, viewModel: InspectorViewModel) {
    val levelColor = when (candidate.level) {
        "Reserve" -> androidx.compose.ui.graphics.Color(0xFF7B1FA2)
        "Gold" -> androidx.compose.ui.graphics.Color(0xFFF9A825)
        "Silver" -> androidx.compose.ui.graphics.Color(0xFF78909C)
        else -> androidx.compose.ui.graphics.Color(0xFF8D6E63)
    }
    var showDetails by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scoringStates by viewModel.scoringStates.collectAsState()
    val myScore = scoringStates[candidate.id]

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(candidate.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("@${candidate.username} · ${candidate.city} · ${candidate.age} лет",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${candidate.direction} · ${candidate.eventsCount} мероприятий",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${candidate.rating}", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Surface(color = levelColor, shape = MaterialTheme.shapes.small) {
                        Text(candidate.level, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = { showDetails = !showDetails }) {
                    Text(if (showDetails) "Скрыть" else "Подробнее")
                }
                OutlinedButton(
                    onClick = { viewModel.scoreCandidate(candidate) },
                    enabled = myScore?.isLoading != true,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    if (myScore?.isLoading == true) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI-оценка")
                }
                OutlinedButton(
                    onClick = { exportPdf(context, candidate) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
            }

            if (myScore?.result != null) {
                // Текст AI не показываем — только кнопка PDF
                OutlinedButton(
                    onClick = { exportPdf(context, candidate, aiScore = myScore.result) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Скачать отчёт PDF с AI-оценкой")
                }
            }

            if (showDetails) {
                Divider()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Достижения", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    candidate.achievements.forEach { (title, pts) ->
                        Text("• $title — $pts баллов", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Прогноз: ${candidate.forecast}",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

private fun exportPdf(context: Context, candidate: StubParticipant, aiScore: String? = null) {
    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = doc.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val titlePaint = Paint().apply { color = Color.BLACK; textSize = 20f; isFakeBoldText = true }
    val bodyPaint = Paint().apply { color = Color.BLACK; textSize = 14f }
    val subPaint = Paint().apply { color = Color.DKGRAY; textSize = 12f }
    val accentPaint = Paint().apply { color = Color.rgb(0, 120, 200); textSize = 13f }
    var y = 60f
    canvas.drawText("Профиль участника CRONOS", 40f, y, titlePaint)
    y += 30f; canvas.drawLine(40f, y, 555f, y, subPaint); y += 20f
    canvas.drawText(candidate.name, 40f, y, titlePaint.apply { textSize = 18f }); y += 24f
    canvas.drawText("@${candidate.username}  ·  ${candidate.city}  ·  ${candidate.age} лет", 40f, y, subPaint); y += 20f
    canvas.drawText("Направление: ${candidate.direction}  ·  Мероприятий: ${candidate.eventsCount}", 40f, y, bodyPaint); y += 20f
    canvas.drawText("Рейтинг: ${candidate.rating}  ·  Уровень: ${candidate.level}", 40f, y, bodyPaint); y += 30f
    canvas.drawText("Достижения:", 40f, y, titlePaint.apply { textSize = 15f }); y += 22f
    candidate.achievements.forEach { (title, pts) ->
        canvas.drawText("  •  $title  —  $pts баллов", 40f, y, bodyPaint.apply { textSize = 13f }); y += 20f
    }
    y += 10f; canvas.drawLine(40f, y, 555f, y, subPaint); y += 18f
    canvas.drawText("Прогноз:", 40f, y, titlePaint.apply { textSize = 14f }); y += 20f
    var line = ""
    candidate.forecast.split(" ").forEach { word ->
        if ((line + word).length > 70) { canvas.drawText(line.trim(), 40f, y, accentPaint); y += 18f; line = "$word " }
        else line += "$word "
    }
    if (line.isNotBlank()) canvas.drawText(line.trim(), 40f, y, accentPaint)
    y += 30f
    // AI-оценка если есть
    if (aiScore != null) {
        val cleanScore = aiScore.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1").replace(Regex("\\*(.+?)\\*"), "$1")
        canvas.drawLine(40f, y, 555f, y, subPaint); y += 18f
        canvas.drawText("AI-оценка кандидата:", 40f, y, titlePaint.apply { textSize = 14f }); y += 20f
        var scoreLine = ""
        cleanScore.split(" ").forEach { word ->
            if ((scoreLine + word).length > 70) { canvas.drawText(scoreLine.trim(), 40f, y, bodyPaint.apply { textSize = 12f }); y += 16f; scoreLine = "$word " }
            else scoreLine += "$word "
        }
        if (scoreLine.isNotBlank()) canvas.drawText(scoreLine.trim(), 40f, y, bodyPaint.apply { textSize = 12f })
        y += 24f
    }
    canvas.drawLine(40f, y, 555f, y, subPaint); y += 16f
    canvas.drawText("Сформировано платформой CRONOS", 40f, y, subPaint)
    doc.finishPage(page)
    val fileName = "${candidate.name.replace(" ", "_")}.pdf"
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { doc.writeTo(it) }
                values.clear(); values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                Toast.makeText(context, "PDF сохранён: $fileName", Toast.LENGTH_LONG).show()
            }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()
            val file = java.io.File(dir, fileName)
            doc.writeTo(file.outputStream())
            Toast.makeText(context, "PDF сохранён: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Ошибка PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        doc.close()
    }
}

private fun exportComparePdf(context: Context, a: StubParticipant, b: StubParticipant, aiResult: String) {
    val doc = PdfDocument()
    val titlePaint = Paint().apply { color = Color.BLACK; textSize = 16f; isFakeBoldText = true }
    val bodyPaint = Paint().apply { color = Color.BLACK; textSize = 12f }
    val subPaint = Paint().apply { color = Color.DKGRAY; textSize = 11f }
    val accentPaint = Paint().apply { color = Color.rgb(0, 80, 160); textSize = 12f }
    val pageWidth = 595; val pageHeight = 842
    val marginLeft = 40f; val marginRight = 555f; val marginBottom = 810f
    var pageNum = 1
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
    var page = doc.startPage(pageInfo)
    var canvas: Canvas = page.canvas
    var y = 50f

    fun newPage() {
        canvas.drawText("Стр. $pageNum · CRONOS AI-сравнение", marginLeft, marginBottom + 16f, subPaint)
        doc.finishPage(page)
        pageNum++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
        page = doc.startPage(pageInfo)
        canvas = page.canvas
        y = 40f
    }

    fun checkY(needed: Float = 18f) { if (y + needed > marginBottom) newPage() }

    fun drawWrapped(text: String, paint: Paint, lineHeight: Float = 17f) {
        val maxChars = ((marginRight - marginLeft) / (paint.textSize * 0.55f)).toInt()
        var line = ""
        text.split(" ").forEach { word ->
            if ((line + word).length > maxChars) {
                checkY(lineHeight)
                canvas.drawText(line.trim(), marginLeft, y, paint); y += lineHeight; line = "$word "
            } else line += "$word "
        }
        if (line.isNotBlank()) { checkY(lineHeight); canvas.drawText(line.trim(), marginLeft, y, paint); y += lineHeight }
    }

    // Шапка
    canvas.drawText("AI-сравнение кандидатов · CRONOS", marginLeft, y, titlePaint.apply { textSize = 18f }); y += 26f
    canvas.drawLine(marginLeft, y, marginRight, y, subPaint); y += 18f

    // Карточки кандидатов
    canvas.drawText("Кандидат А: ${a.name}", marginLeft, y, titlePaint.apply { textSize = 14f }); y += 17f
    canvas.drawText("${a.age} лет · ${a.city} · ${a.direction} · ${a.level} · ${a.rating} баллов · ${a.eventsCount} событий", marginLeft, y, subPaint); y += 14f
    canvas.drawText("Достижения: ${a.achievements.joinToString(", ") { it.first }}", marginLeft, y, subPaint); y += 20f

    canvas.drawText("Кандидат Б: ${b.name}", marginLeft, y, titlePaint.apply { textSize = 14f }); y += 17f
    canvas.drawText("${b.age} лет · ${b.city} · ${b.direction} · ${b.level} · ${b.rating} баллов · ${b.eventsCount} событий", marginLeft, y, subPaint); y += 14f
    canvas.drawText("Достижения: ${b.achievements.joinToString(", ") { it.first }}", marginLeft, y, subPaint); y += 22f

    canvas.drawLine(marginLeft, y, marginRight, y, subPaint); y += 18f
    canvas.drawText("Заключение AI:", marginLeft, y, titlePaint.apply { textSize = 14f }); y += 20f

    // Текст AI — разбиваем по строкам с переносом страниц
    val clean = aiResult
        .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
        .replace(Regex("\\*(.+?)\\*"), "$1")
        .replace(Regex("#{1,6}\\s?"), "")
        .trim()

    // Разбиваем по абзацам (нумерованные разделы)
    clean.split("\n").forEach { paragraph ->
        val trimmed = paragraph.trim()
        if (trimmed.isBlank()) { y += 8f; return@forEach }
        // Заголовки разделов (начинаются с цифры или слова-заголовка)
        val isHeader = trimmed.matches(Regex("^\\d+\\..*")) || trimmed.endsWith(":")
        checkY(20f)
        if (isHeader) {
            y += 4f
            drawWrapped(trimmed, titlePaint.apply { textSize = 13f; isFakeBoldText = true }, 18f)
            y += 2f
        } else {
            drawWrapped(trimmed, accentPaint, 16f)
            y += 4f
        }
    }

    y += 16f; checkY(30f)
    canvas.drawLine(marginLeft, y, marginRight, y, subPaint); y += 14f
    canvas.drawText("Сформировано платформой CRONOS · AI-анализ", marginLeft, y, subPaint)

    // Закрываем последнюю страницу
    canvas.drawText("Стр. $pageNum · CRONOS AI-сравнение", marginLeft, marginBottom + 16f, subPaint)
    doc.finishPage(page)

    val fileName = "Сравнение_${a.name.split(" ").first()}_${b.name.split(" ").first()}.pdf"
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { doc.writeTo(it) }
                values.clear(); values.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
                Toast.makeText(context, "PDF сохранён: $fileName", Toast.LENGTH_LONG).show()
            }
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()
            val file = java.io.File(dir, fileName)
            doc.writeTo(file.outputStream())
            Toast.makeText(context, "PDF сохранён: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Ошибка PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        doc.close()
    }
}
