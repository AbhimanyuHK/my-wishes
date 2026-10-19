package com.knockheart.mywishes

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.knockheart.mywishes.card.CardPalette
import com.knockheart.mywishes.card.CardGenerationRequest
import com.knockheart.mywishes.card.CardGeneratorEngine
import com.knockheart.mywishes.data.catalog.AssetGreetingCatalogRepository
import com.knockheart.mywishes.data.catalog.Greeting
import com.knockheart.mywishes.data.catalog.Occasion
import com.knockheart.mywishes.data.model.AppLanguage
import com.knockheart.mywishes.data.model.UserProfile
import com.knockheart.mywishes.data.preferences.UserPreferencesRepository
import com.knockheart.mywishes.data.profile.ProfileImageStorage
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val preferencesRepository = remember { UserPreferencesRepository(this@MainActivity) }
            val preferences by preferencesRepository.preferences.collectAsState(
                initial = com.knockheart.mywishes.data.model.UserPreferences()
            )
            val systemConfiguration = LocalConfiguration.current
            val localizedConfiguration = remember(systemConfiguration, preferences.preferredLanguage) {
                Configuration(systemConfiguration).apply {
                    setLocale(Locale.forLanguageTag(preferences.preferredLanguage.code))
                }
            }
            val localizedContext = remember(localizedConfiguration) {
                createConfigurationContext(localizedConfiguration)
            }
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfiguration
            ) {
                MyWishesTheme(isDarkTheme = preferences.isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        WishesHome()
                    }
                }
            }
        }
    }
}

@Composable
private fun MyWishesTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme(),
        content = content
    )
}

@Composable
private fun WishesHome() {
    val context = LocalContext.current
    val preferencesRepository = remember { UserPreferencesRepository(context) }
    val catalogRepository = remember { AssetGreetingCatalogRepository(context) }
    val preferences by preferencesRepository.preferences.collectAsState(
        initial = com.knockheart.mywishes.data.model.UserPreferences()
    )
    val scope = rememberCoroutineScope()

    var occasions by remember { mutableStateOf<List<Occasion>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }
    val filteredOccasions = remember(occasions, searchQuery, favoritesOnly, preferences.favoriteGreetingIds) {
        val query = searchQuery.trim()
        val candidateOccasions = if (query.isEmpty()) {
            occasions.map { occasion -> occasion to occasion.greetings }
        } else {
            occasions.mapNotNull { occasion ->
                val occasionMatches = occasion.title.contains(query, ignoreCase = true) ||
                    occasion.description.contains(query, ignoreCase = true)
                val matchingGreetings = if (occasionMatches) {
                    occasion.greetings
                } else {
                    occasion.greetings.filter { it.text.contains(query, ignoreCase = true) }
                }
                if (matchingGreetings.isNotEmpty()) occasion to matchingGreetings else null
            }
        }
        candidateOccasions.mapNotNull { (occasion, greetings) ->
            val visible = if (favoritesOnly) greetings.filter { it.id in preferences.favoriteGreetingIds } else greetings
            if (visible.isNotEmpty()) occasion to visible else null
        }
    }
    var selectedGreeting by remember { mutableStateOf<Greeting?>(null) }
    var selectedOccasion by remember { mutableStateOf<Occasion?>(null) }
    var displayName by remember { mutableStateOf("") }
    var signature by remember { mutableStateOf("") }
    var customGreeting by remember { mutableStateOf("") }
    var photoBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    val profileImageStorage = remember { ProfileImageStorage(context) }
    var profileLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(preferences.profile, profileLoaded) {
        if (!profileLoaded) {
            displayName = preferences.profile.displayName
            signature = preferences.profile.cardSignature
            photoBitmap = profileImageStorage.load(preferences.profile.profileImagePath)
            profileLoaded = true
        }
    }

    LaunchedEffect(preferences.preferredLanguage) {
        loading = true
        errorMessage = null
        try {
            occasions = catalogRepository.loadCatalog(preferences.preferredLanguage.code).occasions
            selectedOccasion = occasions.firstOrNull()
            selectedGreeting = selectedOccasion?.greetings?.firstOrNull()
        } catch (error: Exception) {
            errorMessage = error.localizedMessage ?: context.getString(R.string.catalog_load_error)
        } finally {
            loading = false
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val privatePath = profileImageStorage.copyFromContentUri(uri.toString())
                val savedBitmap = profileImageStorage.load(privatePath)
                if (savedBitmap == null) {
                    errorMessage = context.getString(R.string.image_save_error)
                } else {
                    photoBitmap = savedBitmap
                    scope.launch {
                        preferencesRepository.saveProfile(
                            UserProfile(
                                displayName = displayName,
                                profileImagePath = privatePath,
                                cardSignature = signature
                            )
                        )
                    }
                    errorMessage = null
                }
            } catch (error: Exception) {
                errorMessage = error.localizedMessage ?: context.getString(R.string.image_save_error)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                Text(stringResource(R.string.home_subtitle))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.dark_theme), modifier = Modifier.padding(end = 8.dp))
                    Switch(
                        checked = preferences.isDarkTheme,
                        onCheckedChange = { enabled ->
                            scope.launch { preferencesRepository.setDarkThemeEnabled(enabled) }
                        },
                        modifier = Modifier,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                OutlinedButton(onClick = { languageMenuExpanded = true }) {
                    Text(preferences.preferredLanguage.nativeName)
                }
                DropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false }
                ) {
                    AppLanguage.supported.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language.nativeName) },
                            onClick = {
                                languageMenuExpanded = false
                                scope.launch { preferencesRepository.setPreferredLanguage(language) }
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.your_name_optional)) },
            singleLine = true
        )
        OutlinedTextField(
            value = signature,
            onValueChange = { signature = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.card_signature_optional)) },
            singleLine = true
        )

        Text(stringResource(R.string.choose_occasion), style = MaterialTheme.typography.titleLarge)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { favoritesOnly = !favoritesOnly }
        ) {
            Text(if (favoritesOnly) stringResource(R.string.show_all_greetings) else stringResource(R.string.favorites_only))
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_greetings)) },
            singleLine = true
        )
        if (loading) Text(stringResource(R.string.loading_greetings))
        if (!loading && filteredOccasions.isEmpty()) {
            Text(
                text = stringResource(R.string.no_greetings_found),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    searchQuery = ""
                    favoritesOnly = false
                }
            ) {
                Text(stringResource(R.string.clear_greeting_filters))
            }
        }
        filteredOccasions.forEach { (occasion, visibleGreetings) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    selectedOccasion = occasion
                    selectedGreeting = visibleGreetings.firstOrNull()
                    customGreeting = ""
                    previewBitmap = null
                }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(occasion.title, style = MaterialTheme.typography.titleMedium)
                    if (occasion.description.isNotBlank()) {
                        Text(occasion.description, style = MaterialTheme.typography.bodySmall)
                    }
                    if (selectedOccasion?.id == occasion.id) {
                        Spacer(Modifier.height(8.dp))
                        visibleGreetings.forEachIndexed { index, greeting ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedGreeting = greeting
                                    customGreeting = ""
                                    previewBitmap = null
                                }.padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedGreeting?.id == greeting.id) "● " else "○ ",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(greeting.text, modifier = Modifier.weight(1f))
                                val isFavorite = greeting.id in preferences.favoriteGreetingIds
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            preferencesRepository.setFavoriteGreeting(greeting.id, !isFavorite)
                                        }
                                    },
                                    modifier = Modifier.semantics {
                                        contentDescription = context.getString(
                                            if (isFavorite) R.string.remove_favorite_accessibility
                                            else R.string.add_favorite_accessibility,
                                            greeting.text
                                        )
                                    }
                                ) {
                                    Text(
                                        text = if (isFavorite) "★" else "☆",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            if (index < visibleGreetings.lastIndex) {
                                Spacer(Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
        }

        if (selectedGreeting != null) {
            OutlinedTextField(
                value = customGreeting,
                onValueChange = { customGreeting = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.custom_greeting_optional)) },
                placeholder = { Text(stringResource(R.string.custom_greeting_hint)) },
                minLines = 2,
                maxLines = 5
            )
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { photoPicker.launch("image/*") }
        ) {
            Text(if (photoBitmap == null) stringResource(R.string.add_photo_optional) else stringResource(R.string.change_selected_photo))
        }
        if (photoBitmap != null) {
            Image(
                bitmap = photoBitmap!!.asImageBitmap(),
                contentDescription = stringResource(R.string.selected_photo),
                modifier = Modifier.size(112.dp).align(Alignment.CenterHorizontally)
            )
        }

        Text(stringResource(R.string.card_color_palette), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                listOf(CardPalette.SUNSET, CardPalette.OCEAN, CardPalette.FOREST),
                listOf(CardPalette.ROSE, CardPalette.NIGHT)
            ).forEach { paletteRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paletteRow.forEach { palette ->
                        val paletteLabel = when (palette) {
                            CardPalette.SUNSET -> stringResource(R.string.palette_sunset)
                            CardPalette.OCEAN -> stringResource(R.string.palette_ocean)
                            CardPalette.FOREST -> stringResource(R.string.palette_forest)
                            CardPalette.ROSE -> stringResource(R.string.palette_rose)
                            CardPalette.NIGHT -> stringResource(R.string.palette_night)
                        }
                        val isSelected = preferences.cardPaletteId == palette.id
                        if (isSelected) {
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = { scope.launch { preferencesRepository.setCardPalette(palette.id) } }
                            ) {
                                Text(paletteLabel)
                            }
                        } else {
                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = { scope.launch { preferencesRepository.setCardPalette(palette.id) } }
                            ) {
                                Text(
                                    text = paletteLabel,
                                    color = androidx.compose.ui.graphics.Color(palette.accentColor)
                                )
                            }
                        }
                    }
                    repeat(3 - paletteRow.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedGreeting != null && !loading,
            onClick = {
                val greeting = selectedGreeting ?: return@Button
                try {
                    val request = CardGenerationRequest(
                        greetingText = customGreeting.trim().ifBlank { greeting.text },
                        occasionTitle = selectedOccasion?.title,
                        signature = signature.ifBlank { displayName },
                        photo = photoBitmap,
                        backgroundColor = CardPalette.fromId(preferences.cardPaletteId).backgroundColor,
                        accentColor = CardPalette.fromId(preferences.cardPaletteId).accentColor,
                        textColor = CardPalette.fromId(preferences.cardPaletteId).textColor
                    )
                    previewBitmap = CardGeneratorEngine(context).render(request)
                    errorMessage = null
                    scope.launch {
                        preferencesRepository.saveProfile(
                            UserProfile(
                                displayName = displayName,
                                profileImagePath = preferences.profile.profileImagePath,
                                cardSignature = signature
                            )
                        )
                    }
                } catch (error: Exception) {
                    errorMessage = error.localizedMessage ?: context.getString(R.string.card_generate_error)
                }
            }
        ) {
            Text(stringResource(R.string.generate_card_preview))
        }

        previewBitmap?.let { bitmap ->
            Text(stringResource(R.string.preview), style = MaterialTheme.typography.titleLarge)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.generated_card_preview),
                modifier = Modifier.fillMaxWidth().height(320.dp)
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    try {
                        val file = CardGeneratorEngine(context).exportPng(
                            CardGenerationRequest(
                                greetingText = customGreeting.trim().ifBlank {
                                    selectedGreeting?.text ?: return@Button
                                },
                                occasionTitle = selectedOccasion?.title,
                                signature = signature.ifBlank { displayName },
                                photo = photoBitmap,
                                backgroundColor = CardPalette.fromId(preferences.cardPaletteId).backgroundColor,
                                accentColor = CardPalette.fromId(preferences.cardPaletteId).accentColor,
                                textColor = CardPalette.fromId(preferences.cardPaletteId).textColor
                            )
                        )
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_your_wish)))
                    } catch (error: Exception) {
                        errorMessage = error.localizedMessage ?: context.getString(R.string.card_share_error)
                    }
                }
            ) {
                Text(stringResource(R.string.share_card))
            }
        }

        errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(12.dp))
    }
}
