package com.cronos.app.ui.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.cronos.app.data.stub.AnticheatUser
import com.cronos.app.data.stub.BanStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnticheatScreen(navController: NavController, viewModel: AnticheatViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var filterStatus by remember { mutableStateOf<BanStatus?>(null) }

    val displayed = state.users
        .let { if (filterStatus != null) it.filter { u -> u.status == filterStatus } else it }

    val bannedCount = state.users.count { it.status == BanStatus.BANNED }
    val suspiciousCount = state.users.count { it.status == BanStatus.SUSPICIOUS }
    val cleanCount = state.users.count { it.status == BanStatus.CLEAN }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Античит")
                        if (state.isStreaming) {
                            Surface(
                                color = Color(0xFF1B5E20),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(8.dp),
                                        color = Color.White,
                                        strokeWidth = 1.5.dp
                                    )
                                    Text("LIVE", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    // Переключатель AI
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome, null,
                            tint = if (state.aiEnabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Switch(
                            checked = state.aiEnabled,
                            onCheckedChange = { viewModel.toggleAi() },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Статус AI
            AnimatedVisibility(!state.aiEnabled) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE65100).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                        Text(
                            "AI отключён — ручная проверка. Нажмите на пользователя для изменения статуса.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Сводка
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Security, null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                "Мониторинг · ${state.users.size} / 500",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            if (state.aiEnabled) "AI: ВКЛ" else "AI: ВЫКЛ",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.aiEnabled) Color(0xFF388E3C) else Color(0xFFB71C1C),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        AnticheatStat("$bannedCount", "Забанено", Color(0xFFB71C1C))
                        AnticheatStat("$suspiciousCount", "Подозрит.", Color(0xFFE65100))
                        AnticheatStat("$cleanCount", "Чистых", Color(0xFF1B5E20))
                    }

                    // AI-сводка кнопка (только если AI включён)
                    if (state.aiEnabled) {
                        Button(
                            onClick = { viewModel.runAiSummary() },
                            enabled = !state.isSummaryLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isSummaryLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("AI анализирует...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("AI-сводка по платформе")
                            }
                        }
                        if (state.aiSummary != null) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                                Text(
                                    state.aiSummary!!,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            OutlinedButton(
                                onClick = { exportAnticheatPdf(context, state.users, state.aiSummary!!) },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Скачать отчёт безопасности PDF")
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.toggleAi() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Включить AI-анализ")
                        }
                    }
                }
            }

            // Фильтры
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = filterStatus == null, onClick = { filterStatus = null },
                    label = { Text("Все (${state.users.size})") })
                FilterChip(selected = filterStatus == BanStatus.BANNED, onClick = { filterStatus = BanStatus.BANNED },
                    label = { Text("Забанены ($bannedCount)") })
                FilterChip(selected = filterStatus == BanStatus.SUSPICIOUS, onClick = { filterStatus = BanStatus.SUSPICIOUS },
                    label = { Text("Подозрит. ($suspiciousCount)") })
                FilterChip(selected = filterStatus == BanStatus.CLEAN, onClick = { filterStatus = BanStatus.CLEAN },
                    label = { Text("Чистые ($cleanCount)") })
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(displayed, key = { it.id }) { user ->
                    AnticheatUserCard(user, aiEnabled = state.aiEnabled, onStatusChange = { status, reason ->
                        viewModel.setUserStatus(user.id, status, reason)
                    })
                }
            }
        }
    }
}

@Composable
private fun AnticheatUserCard(
    user: AnticheatUser,
    aiEnabled: Boolean,
    onStatusChange: (BanStatus, String?) -> Unit
) {
    val (bgColor, statusIcon, statusLabel) = when (user.status) {
        BanStatus.BANNED -> Triple(Color(0xFF7F0000).copy(alpha = 0.15f), Icons.Default.Block, "ЗАБАНЕН")
        BanStatus.SUSPICIOUS -> Triple(Color(0xFFE65100).copy(alpha = 0.12f), Icons.Default.Warning, "ПОДОЗРИТ.")
        BanStatus.CLEAN -> Triple(Color.Transparent, Icons.Default.CheckCircle, "OK")
    }
    val iconTint = when (user.status) {
        BanStatus.BANNED -> Color(0xFFB71C1C)
        BanStatus.SUSPICIOUS -> Color(0xFFE65100)
        BanStatus.CLEAN -> Color(0xFF388E3C)
    }

    var showManualMenu by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.background(bgColor).padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(statusIcon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(user.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (!aiEnabled) {
                            // Иконка ручной проверки
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                        }
                        Text(statusLabel, style = MaterialTheme.typography.labelSmall, color = iconTint, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    "@${user.username} · ${user.rating} баллов · ${user.pointsPerDay.toInt()} б/день",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (user.banReason != null) {
                    Text("⚠ ${user.banReason}", style = MaterialTheme.typography.bodySmall, color = iconTint)
                }
            }

            // Ручная проверка — только если AI выключен
            if (!aiEnabled) {
                Box {
                    IconButton(onClick = { showManualMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = showManualMenu, onDismissRequest = { showManualMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Забанить", color = Color(0xFFB71C1C)) },
                            leadingIcon = { Icon(Icons.Default.Block, null, tint = Color(0xFFB71C1C)) },
                            onClick = { onStatusChange(BanStatus.BANNED, "Ручная блокировка модератором"); showManualMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Предупреждение", color = Color(0xFFE65100)) },
                            leadingIcon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFE65100)) },
                            onClick = { onStatusChange(BanStatus.SUSPICIOUS, "Ручное предупреждение модератора"); showManualMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Снять ограничения", color = Color(0xFF388E3C)) },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF388E3C)) },
                            onClick = { onStatusChange(BanStatus.CLEAN, null); showManualMenu = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnticheatStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun exportAnticheatPdf(context: Context, users: List<AnticheatUser>, aiSummary: String) {
    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = doc.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val titlePaint = Paint().apply { color = android.graphics.Color.BLACK; textSize = 18f; isFakeBoldText = true }
    val bodyPaint = Paint().apply { color = android.graphics.Color.BLACK; textSize = 13f }
    val subPaint = Paint().apply { color = android.graphics.Color.DKGRAY; textSize = 11f }
    val redPaint = Paint().apply { color = android.graphics.Color.rgb(183, 28, 28); textSize = 12f }
    val orangePaint = Paint().apply { color = android.graphics.Color.rgb(230, 81, 0); textSize = 12f }
    val accentPaint = Paint().apply { color = android.graphics.Color.rgb(0, 120, 200); textSize = 12f }
    var y = 50f

    val banned = users.count { it.status == BanStatus.BANNED }
    val suspicious = users.count { it.status == BanStatus.SUSPICIOUS }
    val clean = users.count { it.status == BanStatus.CLEAN }

    canvas.drawText("Отчёт безопасности · CRONOS Античит", 40f, y, titlePaint); y += 26f
    canvas.drawLine(40f, y, 555f, y, subPaint); y += 18f
    canvas.drawText("Всего пользователей: ${users.size}", 40f, y, bodyPaint); y += 18f
    canvas.drawText("Забанено: $banned  |  Подозрительных: $suspicious  |  Чистых: $clean", 40f, y, bodyPaint); y += 24f

    canvas.drawLine(40f, y, 555f, y, subPaint); y += 18f
    canvas.drawText("Заключение AI:", 40f, y, titlePaint.apply { textSize = 14f }); y += 20f
    val cleanSummary = aiSummary.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1").replace(Regex("\\*(.+?)\\*"), "$1")
    var line = ""
    cleanSummary.split(" ").forEach { word ->
        if ((line + word).length > 72) { canvas.drawText(line.trim(), 40f, y, accentPaint); y += 16f; line = "$word " }
        else line += "$word "
    }
    if (line.isNotBlank()) canvas.drawText(line.trim(), 40f, y, accentPaint); y += 24f

    canvas.drawLine(40f, y, 555f, y, subPaint); y += 18f
    canvas.drawText("Топ нарушители:", 40f, y, titlePaint.apply { textSize = 13f }); y += 18f
    users.filter { it.status == BanStatus.BANNED }.take(10).forEach { u ->
        if (y < 800f) {
            canvas.drawText("${u.name} (@${u.username}) — ${u.banReason ?: "нарушение"}", 40f, y, redPaint); y += 15f
        }
    }
    y += 8f
    canvas.drawText("Подозрительные:", 40f, y, titlePaint.apply { textSize = 13f }); y += 18f
    users.filter { it.status == BanStatus.SUSPICIOUS }.take(5).forEach { u ->
        if (y < 800f) {
            canvas.drawText("${u.name} (@${u.username}) — ${u.banReason ?: "подозрительная активность"}", 40f, y, orangePaint); y += 15f
        }
    }
    y += 16f
    canvas.drawLine(40f, y, 555f, y, subPaint); y += 14f
    canvas.drawText("Сформировано платформой CRONOS", 40f, y, subPaint)
    doc.finishPage(page)

    val fileName = "Anticheat_Report_CRONOS.pdf"
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
