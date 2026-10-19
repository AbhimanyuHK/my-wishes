package com.knockheart.mywishes

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
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
import java.io.File
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
    var selectedGreeting by remember { mutableStateOf<Greeting?>(null) }
    var selectedOccasion by remember { mutableStateOf<Occasion?>(null) }
    var displayName by remember { mutableStateOf("") }
    var signature by remember { mutableStateOf("") }
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
            errorMessage = error.localizedMessage ?: "Could not load the offline greeting catalog."
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
                    errorMessage = "The selected image could not be saved."
                } else {
                    photoBitmap = savedBitmap
                    scope.launch {
                        preferencesRepository.saveProfile(
                            UserProfile(displayName = displayName, profileImagePath = privatePath)
                        )
                    }
                    errorMessage = null
                }
            } catch (error: Exception) {
                errorMessage = error.localizedMessage ?: "Could not save the selected image."
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
        if (loading) Text(stringResource(R.string.loading_greetings))
        occasions.forEach { occasion ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    selectedOccasion = occasion
                    selectedGreeting = occasion.greetings.firstOrNull()
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
                        occasion.greetings.forEachIndexed { index, greeting ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedGreeting = greeting
                                    previewBitmap = null
                                }.padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedGreeting?.id == greeting.id) "● " else "○ ",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(greeting.text, modifier = Modifier.weight(1f))
                            }
                            if (index < occasion.greetings.lastIndex) {
                                Spacer(Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
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

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedGreeting != null && !loading,
            onClick = {
                val greeting = selectedGreeting ?: return@Button
                try {
                    val request = CardGenerationRequest(
                        greetingText = greeting.text,
                        occasionTitle = selectedOccasion?.title,
                        signature = signature.ifBlank { displayName },
                        photo = photoBitmap
                    )
                    previewBitmap = CardGeneratorEngine(context).render(request)
                    errorMessage = null
                    scope.launch {
                        preferencesRepository.saveProfile(
                            UserProfile(displayName = displayName, profileImagePath = preferences.profile.profileImagePath)
                        )
                    }
                } catch (error: Exception) {
                    errorMessage = error.localizedMessage ?: "Could not generate the card."
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
                                greetingText = selectedGreeting?.text ?: return@Button,
                                occasionTitle = selectedOccasion?.title,
                                signature = signature.ifBlank { displayName },
                                photo = photoBitmap
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
                        errorMessage = error.localizedMessage ?: "Could not prepare the card for sharing."
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
