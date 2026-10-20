package com.knockheart.mywishes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.knockheart.mywishes.card.CardGenerationRequest
import com.knockheart.mywishes.card.CardGeneratorEngine
import com.knockheart.mywishes.card.CardHistoryStorage
import com.knockheart.mywishes.card.CardPalette
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
import android.content.res.Configuration

private enum class AppRoute { HOME, CATEGORIES, WISH_LIST, EDITOR, HISTORY, PROFILE }
private enum class MainTab(val label: String, val symbol: String) {
    HOME("Home", "⌂"),
    CATEGORIES("Categories", "▦"),
    HISTORY("History", "◷"),
    PROFILE("Profile", "●")
}

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
            androidx.compose.runtime.CompositionLocalProvider(
                LocalActivityResultRegistryOwner provides this@MainActivity,
                androidx.compose.ui.platform.LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfiguration
            ) {
                MyWishesTheme(isDarkTheme = preferences.isDarkTheme) {
                    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        WishesApp()
                    }
                }
            }
        }
    }
}

@Composable
private fun MyWishesTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    val lightScheme = lightColorScheme(
        primary = Color(0xFF7655D9),
        onPrimary = Color.White,
        secondary = Color(0xFFEC8C78),
        background = Color(0xFFFFFAF7),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1ECFA)
    )
    val darkScheme = darkColorScheme(
        primary = Color(0xFFC6B7FF),
        secondary = Color(0xFFFFB6A5)
    )
    MaterialTheme(colorScheme = if (isDarkTheme) darkScheme else lightScheme, content = content)
}

@Composable
private fun WishesApp() {
    val context = LocalContext.current
    val preferencesRepository = remember { UserPreferencesRepository(context) }
    val catalogRepository = remember { AssetGreetingCatalogRepository(context) }
    val profileImageStorage = remember { ProfileImageStorage(context) }
    val historyStorage = remember { CardHistoryStorage(context) }
    val preferences by preferencesRepository.preferences.collectAsState(
        initial = com.knockheart.mywishes.data.model.UserPreferences()
    )
    val scope = rememberCoroutineScope()

    var route by remember { mutableStateOf(AppRoute.HOME) }
    var occasions by remember { mutableStateOf<List<Occasion>>(emptyList()) }
    var selectedOccasion by remember { mutableStateOf<Occasion?>(null) }
    var selectedGreeting by remember { mutableStateOf<Greeting?>(null) }
    var search by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var signature by remember { mutableStateOf("") }
    var profileImagePath by remember { mutableStateOf<String?>(null) }
    var profilePhoto by remember { mutableStateOf<Bitmap?>(null) }
    var useProfilePhoto by remember { mutableStateOf(true) }
    var customWish by remember { mutableStateOf(false) }
    var customWishText by remember { mutableStateOf("") }
    var customOccasionTitle by remember { mutableStateOf("A special wish") }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var recentCards by remember { mutableStateOf<List<File>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var languageMenuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(preferences.preferredLanguage) {
        loading = true
        try {
            occasions = catalogRepository.loadCatalog(preferences.preferredLanguage.code).occasions
            if (selectedOccasion == null) selectedOccasion = occasions.firstOrNull()
        } catch (error: Exception) {
            message = error.localizedMessage ?: "Could not load wishes. Please try again."
        } finally {
            loading = false
        }
    }
    LaunchedEffect(preferences.profile) {
        displayName = preferences.profile.displayName
        signature = preferences.profile.cardSignature
        profileImagePath = preferences.profile.profileImagePath
        profilePhoto = profileImageStorage.load(preferences.profile.profileImagePath)
    }
    LaunchedEffect(Unit) { recentCards = historyStorage.listRecent() }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val previousPath = profileImagePath
                val newPath = profileImageStorage.copyFromContentUri(uri.toString())
                val bitmap = profileImageStorage.load(newPath)
                if (bitmap == null) {
                    message = "Couldn't load that photo."
                } else {
                    profileImagePath = newPath
                    profilePhoto = bitmap
                    scope.launch {
                        preferencesRepository.saveProfile(
                            UserProfile(displayName = displayName, profileImagePath = newPath, cardSignature = signature)
                        )
                        previousPath?.let(profileImageStorage::delete)
                    }
                    message = "Profile photo updated."
                }
            } catch (error: Exception) {
                message = error.localizedMessage ?: "Couldn't save that photo."
            }
        }
    }

    val saveCardLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        val bitmap = previewBitmap
        if (uri != null && bitmap != null) {
            try {
                val output = context.contentResolver.openOutputStream(uri) ?: error("Couldn't open the destination.")
                output.use { stream ->
                    check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "Couldn't save the image." }
                }
                message = "Card saved to your device."
            } catch (error: Exception) {
                message = error.localizedMessage ?: "Couldn't save the card."
            }
        }
    }

    fun openOccasion(occasion: Occasion) {
        selectedOccasion = occasion
        selectedGreeting = null
        search = ""
        route = AppRoute.WISH_LIST
    }

    fun openGreeting(occasion: Occasion, greeting: Greeting) {
        selectedOccasion = occasion
        selectedGreeting = greeting
        customWish = false
        customWishText = ""
        previewBitmap = null
        message = null
        route = AppRoute.EDITOR
    }

    fun generateCard() {
        val greetingText = if (customWish) customWishText.trim() else selectedGreeting?.text.orEmpty()
        if (greetingText.isBlank()) {
            message = "Add a wish message before creating your card."
            return
        }
        try {
            val palette = CardPalette.fromId(preferences.cardPaletteId)
            val bitmap = CardGeneratorEngine(context).render(
                CardGenerationRequest(
                    greetingText = greetingText,
                    occasionTitle = if (customWish) customOccasionTitle.trim().ifBlank { "A special wish" } else selectedOccasion?.title,
                    signature = signature.ifBlank { displayName },
                    photo = if (useProfilePhoto) profilePhoto else null,
                    backgroundColor = palette.backgroundColor,
                    accentColor = palette.accentColor,
                    textColor = palette.textColor
                )
            )
            previewBitmap = bitmap
            historyStorage.save(bitmap)
            recentCards = historyStorage.listRecent()
            message = "Your wish card is ready!"
            scope.launch {
                preferencesRepository.saveProfile(
                    UserProfile(displayName = displayName, profileImagePath = profileImagePath, cardSignature = signature)
                )
            }
        } catch (error: Exception) {
            message = error.localizedMessage ?: "Couldn't create the card. Please try again."
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                MainTab.entries.forEach { tab ->
                    val target = when (tab) {
                        MainTab.HOME -> AppRoute.HOME
                        MainTab.CATEGORIES -> AppRoute.CATEGORIES
                        MainTab.HISTORY -> AppRoute.HISTORY
                        MainTab.PROFILE -> AppRoute.PROFILE
                    }
                    NavigationBarItem(
                        selected = route == target || (tab == MainTab.CATEGORIES && route in setOf(AppRoute.WISH_LIST, AppRoute.EDITOR)),
                        onClick = { route = target; message = null },
                        icon = { Text(tab.symbol, style = MaterialTheme.typography.titleMedium) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Knockheart", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Text("My Wishes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                if (profilePhoto != null) {
                    Image(
                        bitmap = profilePhoto!!.asImageBitmap(),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(52.dp).clip(CircleShape).clickable { route = AppRoute.PROFILE }
                    )
                } else {
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).clickable { route = AppRoute.PROFILE },
                        contentAlignment = Alignment.Center
                    ) { Text("☺", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary) }
                }
            }

            when (route) {
                AppRoute.HOME -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(26.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(if (displayName.isBlank()) "Make someone's day ✨" else "Hello, $displayName! ✨", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("A little wish can make a big difference. Create something lovely today.")
                            Button(onClick = { route = AppRoute.CATEGORIES }, modifier = Modifier.fillMaxWidth()) {
                                Text("Explore wishes  →")
                            }
                            OutlinedButton(onClick = {
                                customWish = true
                                selectedGreeting = null
                                customWishText = ""
                                customOccasionTitle = "A special wish"
                                previewBitmap = null
                                route = AppRoute.EDITOR
                            }, modifier = Modifier.fillMaxWidth()) {
                                Text("✦  Create a custom card")
                            }
                        }
                    }
                    Text("Browse by occasion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    occasions.chunked(2).forEach { pair ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { occasion ->
                                CategoryTile(occasion, Modifier.weight(1f)) { openOccasion(occasion) }
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    if (recentCards.isNotEmpty()) {
                        Text("Recently created", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            recentCards.take(3).forEach { file ->
                                HistoryThumbnail(file, Modifier.weight(1f)) {
                                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                    if (bitmap != null) { previewBitmap = bitmap; route = AppRoute.HISTORY }
                                }
                            }
                        }
                        OutlinedButton(onClick = { route = AppRoute.HISTORY }, modifier = Modifier.fillMaxWidth()) { Text("View all history") }
                    }
                }
                AppRoute.CATEGORIES -> {
                    Text("Find the perfect wish", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Choose an occasion to explore ready-to-use messages.")
                    OutlinedTextField(
                        value = search, onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        label = { Text("Search categories") }
                    )
                    val visible = occasions.filter {
                        it.title.contains(search, ignoreCase = true) || it.description.contains(search, ignoreCase = true)
                    }
                    visible.chunked(2).forEach { pair ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { occasion -> CategoryTile(occasion, Modifier.weight(1f)) { openOccasion(occasion) } }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    if (visible.isEmpty()) Text("No categories match your search.")
                }
                AppRoute.WISH_LIST -> {
                    OutlinedButton(onClick = { route = AppRoute.CATEGORIES }) { Text("← All categories") }
                    Text(selectedOccasion?.title ?: "Wishes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(selectedOccasion?.description.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    selectedOccasion?.greetings.orEmpty().forEach { greeting ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { openGreeting(selectedOccasion!!, greeting) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(greeting.text, style = MaterialTheme.typography.bodyLarge)
                                    Text("Tap to design this card  →", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                                }
                                Text("♡", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.clickable {
                                        scope.launch {
                                            val isFavorite = greeting.id in preferences.favoriteGreetingIds
                                            preferencesRepository.setFavoriteGreeting(greeting.id, !isFavorite)
                                        }
                                    }.padding(8.dp))
                            }
                        }
                    }
                    OutlinedButton(onClick = {
                        customWish = true
                        customWishText = ""
                        customOccasionTitle = selectedOccasion?.title ?: "A special wish"
                        route = AppRoute.EDITOR
                    }, modifier = Modifier.fillMaxWidth()) { Text("＋ Write my own wish") }
                }
                AppRoute.EDITOR -> {
                    OutlinedButton(onClick = {
                        route = if (customWish) AppRoute.HOME else AppRoute.WISH_LIST
                        previewBitmap = null
                    }) { Text("← Back") }
                    Text(if (customWish) "Create your own card" else "Make it yours", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Personalize your message, then create a shareable square greeting card.")
                    if (customWish) {
                        OutlinedTextField(
                            value = customOccasionTitle, onValueChange = { customOccasionTitle = it },
                            modifier = Modifier.fillMaxWidth(), label = { Text("Card title / occasion") }, singleLine = true
                        )
                        OutlinedTextField(
                            value = customWishText, onValueChange = { customWishText = it },
                            modifier = Modifier.fillMaxWidth(), label = { Text("Your custom wish") },
                            placeholder = { Text("Write a message from your heart…") }, minLines = 4, maxLines = 8
                        )
                    } else {
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(selectedOccasion?.title.orEmpty(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Text(selectedGreeting?.text.orEmpty(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                            }
                        }
                        OutlinedTextField(
                            value = customWishText, onValueChange = { customWishText = it },
                            modifier = Modifier.fillMaxWidth(), label = { Text("Edit message (optional)") },
                            placeholder = { Text("Leave empty to use the selected wish") }, minLines = 2, maxLines = 6
                        )
                    }
                    Text("Card style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(CardPalette.SUNSET, CardPalette.OCEAN, CardPalette.FOREST, CardPalette.ROSE, CardPalette.NIGHT).forEach { palette ->
                            val selected = preferences.cardPaletteId == palette.id
                            Box(
                                Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp))
                                    .background(Color(palette.backgroundColor))
                                    .clickable { scope.launch { preferencesRepository.setCardPalette(palette.id) } }
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(9.dp)).background(Color(palette.accentColor).copy(alpha = if (selected) 0.85f else 0.35f)), contentAlignment = Alignment.Center) {
                                    Text(if (selected) "✓" else " ", color = Color.White)
                                }
                            }
                        }
                    }
                    if (profilePhoto != null) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Include my profile photo", modifier = Modifier.weight(1f))
                            Switch(checked = useProfilePhoto, onCheckedChange = { useProfilePhoto = it })
                        }
                    }
                    OutlinedButton(onClick = { route = AppRoute.PROFILE }, modifier = Modifier.fillMaxWidth()) { Text("Edit profile & photo") }
                    Button(
                        onClick = { generateCard() },
                        enabled = if (customWish) customWishText.isNotBlank() else selectedGreeting != null,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("✨ Generate wish card") }

                    previewBitmap?.let { bitmap ->
                        Text("Preview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Image(bitmap.asImageBitmap(), contentDescription = "Generated greeting card preview", contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)))
                        OutlinedButton(onClick = { saveCardLauncher.launch("my-wish.png") }, modifier = Modifier.fillMaxWidth()) { Text("Save image to device") }
                        Button(onClick = {
                            try {
                                val file = File(context.cacheDir, "my-wishes-share.png")
                                file.outputStream().use { stream ->
                                    check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "Couldn't prepare the image to share." }
                                }
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share your wish"))
                            } catch (error: Exception) {
                                message = error.localizedMessage ?: "Couldn't share the card."
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text("Share wish card") }
                    }
                }
                AppRoute.HISTORY -> {
                    Text("Your creations", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Your latest cards stay on this device.")
                    if (recentCards.isEmpty()) {
                        EmptyState("No cards yet", "Create a wish card and it will appear here.")
                        Button(onClick = { route = AppRoute.CATEGORIES }, modifier = Modifier.fillMaxWidth()) { Text("Create your first card") }
                    } else {
                        recentCards.forEach { file ->
                            Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    HistoryThumbnail(file, Modifier.size(94.dp)) {
                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                        if (bitmap != null) { previewBitmap = bitmap; message = null }
                                    }
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(file.name.removeSuffix(".png"), style = MaterialTheme.typography.labelLarge, maxLines = 1)
                                        OutlinedButton(onClick = {
                                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                            if (bitmap != null) { previewBitmap = bitmap; message = null } else message = "Couldn't open this card."
                                        }) { Text("Preview") }
                                    }
                                    OutlinedButton(onClick = {
                                        if (historyStorage.delete(file)) {
                                            recentCards = historyStorage.listRecent()
                                            message = "Card deleted."
                                        } else message = "Couldn't delete this card."
                                    }) { Text("Delete") }
                                }
                            }
                        }
                    }
                    previewBitmap?.let { bitmap ->
                        Text("Selected card preview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Image(bitmap.asImageBitmap(), contentDescription = "Selected saved card", contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)))
                        OutlinedButton(onClick = { saveCardLauncher.launch("my-wish.png") }, modifier = Modifier.fillMaxWidth()) { Text("Save selected card") }
                    }
                }
                AppRoute.PROFILE -> {
                    Text("Your profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Add your details once and use them on the cards you create.")
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        if (profilePhoto != null) {
                            Image(profilePhoto!!.asImageBitmap(), contentDescription = "Your profile photo", contentScale = ContentScale.Crop,
                                modifier = Modifier.size(124.dp).clip(CircleShape))
                        } else {
                            Box(Modifier.size(124.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                                Text("☺", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Button(onClick = { photoPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (profilePhoto == null) "＋ Add profile photo" else "Change profile photo")
                    }
                    OutlinedTextField(value = displayName, onValueChange = { displayName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Your name") }, singleLine = true)
                    OutlinedTextField(value = signature, onValueChange = { signature = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Card signature") }, singleLine = true)
                    Text("App preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Dark theme", fontWeight = FontWeight.Medium)
                            Text("Choose a comfortable appearance.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = preferences.isDarkTheme, onCheckedChange = { scope.launch { preferencesRepository.setDarkThemeEnabled(it) } })
                    }
                    Text("Language", style = MaterialTheme.typography.titleMedium)
                    Box {
                        OutlinedButton(onClick = { languageMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("${preferences.preferredLanguage.nativeName}  ▾")
                        }
                        DropdownMenu(expanded = languageMenuOpen, onDismissRequest = { languageMenuOpen = false }) {
                            AppLanguage.supported.forEach { language ->
                                DropdownMenuItem(text = { Text(language.nativeName) }, onClick = {
                                    languageMenuOpen = false
                                    scope.launch { preferencesRepository.setPreferredLanguage(language) }
                                })
                            }
                        }
                    }
                    Button(onClick = {
                        scope.launch {
                            preferencesRepository.saveProfile(UserProfile(displayName.trim(), profileImagePath, signature.trim()))
                            message = "Profile saved."
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Save profile") }
                    OutlinedButton(onClick = {
                        val oldPath = profileImagePath
                        scope.launch {
                            preferencesRepository.clearProfile()
                            oldPath?.let(profileImageStorage::delete)
                            displayName = ""
                            signature = ""
                            profileImagePath = null
                            profilePhoto = null
                            message = "Profile cleared."
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Clear profile") }
                }
            }
            if (loading) Text("Loading wishes…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            message?.let { Text(it, color = if (it.contains("couldn't", true) || it.contains("error", true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CategoryTile(occasion: Occasion, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val symbols = listOf("🎂", "💐", "❤️", "🌼", "🎉", "🌙", "✨", "🎈")
    val symbol = symbols[(occasion.id.hashCode() and Int.MAX_VALUE) % symbols.size]
    Card(
        modifier = modifier.heightIn(min = 132.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(symbol, style = MaterialTheme.typography.headlineMedium)
            Text(occasion.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("${occasion.greetings.size} wishes  →", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun HistoryThumbnail(file: File, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bitmap = remember(file.absolutePath, file.lastModified()) { BitmapFactory.decodeFile(file.absolutePath) }
    if (bitmap != null) {
        Image(bitmap.asImageBitmap(), contentDescription = "Saved greeting card", contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick))
    } else {
        Box(modifier.height(94.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Text("Image unavailable", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✨", style = MaterialTheme.typography.displaySmall)
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
