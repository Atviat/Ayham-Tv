package com.ayham.tv.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayham.tv.HomeViewModel
import com.ayham.tv.R
import com.ayham.tv.data.AppInfo
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val cannotOpen = stringResource(R.string.cannot_open)
    val added = stringResource(R.string.added_favorite)
    val removed = stringResource(R.string.removed_favorite)

    val onLaunch: (AppInfo) -> Unit = { app ->
        if (!viewModel.launch(app)) toast(context, cannotOpen)
    }
    val onToggleFavorite: (AppInfo) -> Unit = { app ->
        viewModel.toggleFavorite(app) { isFav -> toast(context, if (isFav) added else removed) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AyhamColors.Background, Color(0xFF1B0B38), AyhamColors.Background)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            item { TopBar() }

            item {
                SectionTitle(stringResource(R.string.section_favorites))
                if (ui.favorites.isEmpty()) {
                    EmptyHint(stringResource(R.string.empty_favorites))
                } else {
                    AppRow(ui.favorites, onLaunch, onToggleFavorite)
                }
            }

            item {
                SectionTitle(stringResource(R.string.section_recent))
                if (ui.recents.isEmpty()) {
                    EmptyHint(stringResource(R.string.empty_recent))
                } else {
                    AppRow(ui.recents, onLaunch, onToggleFavorite)
                }
            }

            item {
                SectionTitle(stringResource(R.string.section_all_apps))
                AppRow(ui.allApps, onLaunch, onToggleFavorite)
            }

            item {
                SectionTitle(stringResource(R.string.section_shortcuts))
                ShortcutsRow(context)
            }
        }
    }
}

@Composable
private fun TopBar() {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(15_000)
        }
    }
    val time = remember(now) { DateFormat.getTimeInstance(DateFormat.SHORT).format(now) }
    val date = remember(now) { DateFormat.getDateInstance(DateFormat.FULL).format(now) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "AYHAM TV",
            color = AyhamColors.PurpleLight,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 6.sp,
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(text = time, color = AyhamColors.TextPrimary, fontSize = 44.sp, fontWeight = FontWeight.Light)
            Text(text = date, color = AyhamColors.TextSecondary, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AyhamColors.TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(text = text, color = AyhamColors.TextSecondary, fontSize = 16.sp)
}

@Composable
private fun AppRow(
    apps: List<AppInfo>,
    onLaunch: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(apps, key = { it.packageName }) { app ->
            AppTile(app, onLaunch, onToggleFavorite)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppTile(
    app: AppInfo,
    onLaunch: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.1f else 1f, label = "tileScale")
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .width(168.dp)
            .scale(scale)
            .onFocusChanged { focused = it.isFocused }
            // Menu button on the remote toggles favorite
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp && event.key == Key.Menu) {
                    onToggleFavorite(app)
                    true
                } else {
                    false
                }
            }
            .background(if (focused) AyhamColors.SurfaceFocused else AyhamColors.Surface, shape)
            .border(if (focused) 3.dp else 0.dp, if (focused) AyhamColors.Purple else Color.Transparent, shape)
            .combinedClickable(
                onClick = { onLaunch(app) },
                onLongClick = { onToggleFavorite(app) },
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(width = 144.dp, height = 90.dp), contentAlignment = Alignment.Center) {
            val icon = app.icon
            if (icon != null) {
                Image(
                    bitmap = icon,
                    contentDescription = app.label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = app.label,
            color = AyhamColors.TextPrimary,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ShortcutsRow(context: Context) {
    val unavailable = stringResource(R.string.cannot_open_settings)
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        ShortcutTile(stringResource(R.string.shortcut_settings)) {
            openSettings(context, Settings.ACTION_SETTINGS, Settings.ACTION_SETTINGS, unavailable)
        }
        ShortcutTile(stringResource(R.string.shortcut_wifi)) {
            openSettings(context, Settings.ACTION_WIFI_SETTINGS, Settings.ACTION_SETTINGS, unavailable)
        }
        ShortcutTile(stringResource(R.string.shortcut_bluetooth)) {
            openSettings(context, Settings.ACTION_BLUETOOTH_SETTINGS, Settings.ACTION_SETTINGS, unavailable)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortcutTile(label: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.08f else 1f, label = "shortcutScale")
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .width(168.dp)
            .height(64.dp)
            .scale(scale)
            .onFocusChanged { focused = it.isFocused }
            .background(if (focused) AyhamColors.SurfaceFocused else AyhamColors.Surface, shape)
            .border(if (focused) 3.dp else 0.dp, if (focused) AyhamColors.Purple else Color.Transparent, shape)
            .combinedClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = AyhamColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Medium)
    }
}

private fun openSettings(context: Context, action: String, fallback: String, unavailableMessage: String) {
    val opened = listOf(action, fallback).distinct().any { candidate ->
        runCatching {
            context.startActivity(Intent(candidate).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }.isSuccess
    }
    if (!opened) toast(context, unavailableMessage)
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
