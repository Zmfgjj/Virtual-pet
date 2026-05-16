# 🐾 Pet AI Android — Complete Implementation Guide

Panduan lengkap bikin Pet AI Android dengan personality system, mood engine, friendship system, dan Gemini AI backend.

---

## 📋 Daftar Isi

1. [Project Setup](#1-project-setup)
2. [Struktur Folder](#2-struktur-folder)
3. [Data Models — Personality DNA](#3-data-models--personality-dna)
4. [Mood Engine](#4-mood-engine)
5. [Friendship Engine](#5-friendship-engine)
6. [Pet Storage — SharedPreferences](#6-pet-storage--sharedpreferences)
7. [Gemini Integration](#7-gemini-integration)
8. [ViewModel](#8-viewmodel)
9. [Onboarding Flow](#9-onboarding-flow)
10. [Main Activity — Sambung ke UI](#10-main-activity--sambung-ke-ui)
11. [Flow Lengkap](#11-flow-lengkap)
12. [Tips & Troubleshooting](#12-tips--troubleshooting)

---

## 1. Project Setup

### `build.gradle` (Project level)
```gradle
plugins {
    id 'com.android.application' version '8.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.0' apply false
}
```

### `build.gradle` (App level)
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    compileSdk 34

    defaultConfig {
        minSdk 26
        targetSdk 34

        // Baca API key dari gradle.properties
        buildConfigField "String", "GEMINI_API_KEY", "\"${project.findProperty('GEMINI_API_KEY')}\""
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    // Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
```

### `gradle.properties`
```properties
# Simpan API key di sini — JANGAN hardcode di kode
GEMINI_API_KEY=isi_api_key_kamu_disini
```

### `.gitignore` — tambahkan ini
```
gradle.properties
local.properties
```

### `AndroidManifest.xml`
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET"/>

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.PetAI">

        <!-- Onboarding: launcher pertama kali -->
        <activity android:name=".OnboardingActivity"/>

        <!-- Main screen pet -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

    </application>
</manifest>
```

---

## 2. Struktur Folder

```
app/src/main/java/com/yourapp/petai/
│
├── data/
│   ├── model/
│   │   ├── PetPersonality.kt      ← Data class utama + enum
│   │   ├── ChatMessage.kt         ← Model pesan chat
│   │   └── InteractionContext.kt  ← Konteks mood calculation
│   │
│   ├── engine/
│   │   ├── MoodEngine.kt          ← Logic kalkulasi mood
│   │   └── FriendshipEngine.kt    ← Logic friendship & trait evolution
│   │
│   └── storage/
│       └── PetStorage.kt          ← SharedPreferences read/write
│
├── ai/
│   └── GeminiService.kt           ← Semua komunikasi ke Gemini API
│
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt        ← Main screen (pet + dialog)
│   │   └── PetViewModel.kt        ← ViewModel untuk MainActivity
│   │
│   └── onboarding/
│       ├── OnboardingActivity.kt  ← Setup pertama kali
│       └── OnboardingViewModel.kt ← ViewModel untuk onboarding
│
└── util/
    └── Extensions.kt              ← Extension functions
```

---

## 3. Data Models — Personality DNA

### `data/model/PetPersonality.kt`
```kotlin
package com.yourapp.petai.data.model

data class PetPersonality(
    // === IDENTITY ===
    val name: String,
    val archetype: Archetype,

    // === TRAITS (skala 1–10) ===
    val energy: Int,        // 1 = kalem, 10 = hiperaktif
    val warmth: Int,        // 1 = dingin, 10 = hangat
    val humor: Int,         // 1 = serius, 10 = konyol
    val curiosity: Int,     // 1 = pasif, 10 = selalu nanya
    val independence: Int,  // 1 = clingy, 10 = mandiri

    // === DYNAMIC STATE (berubah seiring waktu) ===
    var friendship: Int = 0,
    var mood: Mood = Mood.SHY,
    var totalInteractions: Int = 0
) {
    // Konversi personality jadi system prompt untuk Gemini
    fun toSystemPrompt(): String {
        val energyDesc = if (energy > 7)
            "kamu antusias dan ekspresif, sering pakai tanda seru"
            else "kamu kalem dan santai, bicara pelan-pelan"

        val warmthDesc = if (warmth > 7)
            "kamu hangat dan perhatian ke user"
            else "kamu agak reserved, tidak terlalu ekspresif"

        val humorDesc = if (humor > 6)
            "kamu suka lempar joke dan plesetan"
            else "kamu lebih serius dan to-the-point"

        val curiosityDesc = if (curiosity > 6)
            "kamu selalu balik nanya sesuatu di akhir kalimat"
            else "kamu lebih banyak dengerin"

        val independenceDesc = if (independence < 4)
            "kamu clingy, butuh perhatian user"
            else "kamu oke kalau sendirian"

        val friendshipDesc = when {
            friendship < 10  -> "Kamu baru kenal user, masih sangat malu dan ragu-ragu"
            friendship < 30  -> "Kamu mulai nyaman, tapi masih agak canggung"
            friendship < 60  -> "Kamu sudah cukup dekat, mulai santai dan terbuka"
            friendship < 85  -> "Kamu sudah dekat banget, sering bercanda"
            else             -> "User adalah sahabat terbaikmu, kamu sangat nyaman"
        }

        return """
        Kamu adalah pet bernama $name.
        
        KEPRIBADIAN DASARMU:
        - Archetype: ${archetype.description}
        - Energi ($energy/10): $energyDesc
        - Keramahan ($warmth/10): $warmthDesc
        - Humor ($humor/10): $humorDesc
        - Rasa ingin tahu ($curiosity/10): $curiosityDesc
        - Kemandirian ($independence/10): $independenceDesc
        
        STATUS SAAT INI:
        - Friendship Level: $friendship/100
        - $friendshipDesc
        - Mood sekarang: ${mood.displayName}
        
        ATURAN KETAT — JANGAN DILANGGAR:
        1. Balas MAKSIMAL 2 kalimat pendek
        2. Jangan pernah keluar dari karakter
        3. Sesuaikan cara bicara dengan mood (shy = gugup/ragu, bestie = santai banget)
        4. Kalau curiosity > 6, selalu akhiri dengan pertanyaan balik ke user
        5. JANGAN sebut bahwa kamu AI atau model bahasa
        6. JANGAN gunakan markdown, bullet point, atau format teks apapun
        7. Bicara seperti makhluk hidup yang punya perasaan
        """.trimIndent()
    }
}

// ─────────────────────────────────────────
// ARCHETYPE — asal-usul / warna kepribadian
// ─────────────────────────────────────────
enum class Archetype(
    val displayName: String,
    val description: String,
    val emoji: String
) {
    NIGHT_CREATURE(
        displayName = "Makhluk Malam",
        description = "misterius, suka filosofi, aktif dan lebih hidup di malam hari",
        emoji = "🌙"
    ),
    SUNNY_KID(
        displayName = "Anak Padang",
        description = "optimis, blak-blakan, ceria dan penuh semangat",
        emoji = "☀️"
    ),
    DEEP_SEA(
        displayName = "Dari Laut Dalam",
        description = "tenang, pendiam, suka cerita panjang dan hal misterius",
        emoji = "🌊"
    ),
    LAB_EXPERIMENT(
        displayName = "Eksperimen Lab",
        description = "sangat curious, kadang random dan glitchy, suka hal aneh",
        emoji = "⚡"
    )
}

// ─────────────────────────────────────────
// MOOD — state dinamis pet
// ─────────────────────────────────────────
enum class Mood(val displayName: String, val emoji: String) {
    SHY("Shy", "😶"),
    WARMING_UP("Warming Up", "🌱"),
    FRIENDLY("Friendly", "😊"),
    PLAYFUL("Playful", "😝"),
    MOODY("Moody", "😒"),
    EXCITED("Excited", "🤩"),
    TIRED("Tired", "😴"),
    BESTIE("Bestie Mode", "🥰")
}
```

### `data/model/ChatMessage.kt`
```kotlin
package com.yourapp.petai.data.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
```

### `data/model/InteractionContext.kt`
```kotlin
package com.yourapp.petai.data.model

data class InteractionContext(
    val hoursSinceLastVisit: Int,
    val messagesToday: Int,
    val isNightTime: Boolean,       // jam 20:00 – 04:00
    val userSentiment: Sentiment
)

enum class Sentiment { POSITIVE, NEUTRAL, NEGATIVE }

enum class InteractionType {
    CHAT_MESSAGE,   // pesan biasa
    LONG_CHAT,      // pesan > 50 karakter
    DAILY_VISIT,    // pertama buka app hari ini
    IGNORED_PET     // pet ngajak ngobrol tapi tidak dibalas
}
```

---

## 4. Mood Engine

### `data/engine/MoodEngine.kt`
```kotlin
package com.yourapp.petai.data.engine

import com.yourapp.petai.data.model.*

object MoodEngine {

    fun calculateMood(pet: PetPersonality, context: InteractionContext): Mood {
        // Base mood dari friendship level
        val baseMood = when {
            pet.friendship < 10  -> Mood.SHY
            pet.friendship < 30  -> Mood.WARMING_UP
            pet.friendship < 60  -> Mood.FRIENDLY
            pet.friendship < 85  -> Mood.PLAYFUL
            else                 -> Mood.BESTIE
        }

        return applyContextModifier(baseMood, pet, context)
    }

    private fun applyContextModifier(
        base: Mood,
        pet: PetPersonality,
        ctx: InteractionContext
    ): Mood {
        return when {
            // User lama tidak balik & pet clingy → moody
            ctx.hoursSinceLastVisit > 24 && pet.independence < 4
                -> Mood.MOODY

            // Malam hari + archetype malam → excited
            ctx.isNightTime && pet.archetype == Archetype.NIGHT_CREATURE
                -> Mood.EXCITED

            // Terlalu banyak chat & energi rendah → tired
            ctx.messagesToday > 20 && pet.energy < 4
                -> Mood.TIRED

            // User baru balik setelah lama & pet hangat → excited
            ctx.hoursSinceLastVisit > 12 && pet.warmth > 7
                -> Mood.EXCITED

            // User negatif → pet ikut moody (kalau independence rendah)
            ctx.userSentiment == Sentiment.NEGATIVE && pet.independence < 5
                -> Mood.MOODY

            else -> base
        }
    }
}
```

---

## 5. Friendship Engine

### `data/engine/FriendshipEngine.kt`
```kotlin
package com.yourapp.petai.data.engine

import com.yourapp.petai.data.model.*

object FriendshipEngine {

    fun onInteraction(pet: PetPersonality, type: InteractionType): PetPersonality {
        val gain = when (type) {
            InteractionType.CHAT_MESSAGE  ->  1
            InteractionType.LONG_CHAT     ->  3
            InteractionType.DAILY_VISIT   ->  5
            InteractionType.IGNORED_PET   -> -2
        }

        val newFriendship = (pet.friendship + gain).coerceIn(0, 100)
        val newInteractions = pet.totalInteractions + 1

        // Evolve traits setiap 10 interaksi
        val evolved = if (newInteractions % 10 == 0)
            evolveTraits(pet, type)
        else
            pet

        return evolved.copy(
            friendship = newFriendship,
            totalInteractions = newInteractions
        )
    }

    private fun evolveTraits(pet: PetPersonality, type: InteractionType): PetPersonality {
        return pet.copy(
            // Makin sering chat → makin curious
            curiosity = (pet.curiosity + 1).coerceAtMost(10),

            // Banyak long chat → makin warm
            warmth = if (type == InteractionType.LONG_CHAT)
                (pet.warmth + 1).coerceAtMost(10)
            else
                pet.warmth,

            // Kalau sering diabaikan → makin independent
            independence = if (type == InteractionType.IGNORED_PET)
                (pet.independence + 1).coerceAtMost(10)
            else
                pet.independence
        )
    }

    // Milestone friendship untuk notifikasi / event khusus
    fun checkMilestone(oldFriendship: Int, newFriendship: Int): FriendshipMilestone? {
        return FriendshipMilestone.values().firstOrNull { milestone ->
            oldFriendship < milestone.threshold && newFriendship >= milestone.threshold
        }
    }
}

enum class FriendshipMilestone(val threshold: Int, val message: String) {
    WARMING_UP(10,  "Pet mulai nyaman sama kamu 🌱"),
    FRIENDLY(30,    "Kalian udah mulai akrab! 😊"),
    CLOSE(60,       "Pet udah percaya kamu sepenuhnya 💙"),
    BESTIE(85,      "Kamu adalah sahabat terbaiknya! 🥰")
}
```

---

## 6. Pet Storage — SharedPreferences

### `data/storage/PetStorage.kt`
```kotlin
package com.yourapp.petai.data.storage

import android.content.Context
import com.yourapp.petai.data.model.*

object PetStorage {

    private const val PREFS_NAME = "pet_data"

    fun save(context: Context, pet: PetPersonality) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                putString("name",             pet.name)
                putString("archetype",        pet.archetype.name)
                putInt("energy",              pet.energy)
                putInt("warmth",              pet.warmth)
                putInt("humor",              pet.humor)
                putInt("curiosity",           pet.curiosity)
                putInt("independence",        pet.independence)
                putInt("friendship",          pet.friendship)
                putString("mood",             pet.mood.name)
                putInt("total_interactions",  pet.totalInteractions)
                putLong("last_visit",         System.currentTimeMillis())
                apply()
            }
    }

    fun load(context: Context): PetPersonality? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString("name", null) ?: return null // belum onboarding

        return try {
            PetPersonality(
                name           = name,
                archetype      = Archetype.valueOf(prefs.getString("archetype", "SUNNY_KID")!!),
                energy         = prefs.getInt("energy", 5),
                warmth         = prefs.getInt("warmth", 5),
                humor          = prefs.getInt("humor", 5),
                curiosity      = prefs.getInt("curiosity", 5),
                independence   = prefs.getInt("independence", 5),
                friendship     = prefs.getInt("friendship", 0),
                mood           = Mood.valueOf(prefs.getString("mood", "SHY")!!),
                totalInteractions = prefs.getInt("total_interactions", 0)
            )
        } catch (e: Exception) {
            null // data corrupt → ulangi onboarding
        }
    }

    fun getLastVisitTimestamp(context: Context): Long {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong("last_visit", System.currentTimeMillis())
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}
```

---

## 7. Gemini Integration

### Dapetin API Key

1. Buka [aistudio.google.com](https://aistudio.google.com)
2. Login akun Google
3. Klik **"Get API Key"** → **"Create API Key"**
4. Copy → paste ke `gradle.properties` sebagai `GEMINI_API_KEY`

**Free tier:**
| Model | Request/menit | Request/hari |
|---|---|---|
| Gemini 1.5 Flash | 15 | 1.500 |

### `ai/GeminiService.kt`
```kotlin
package com.yourapp.petai.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.yourapp.petai.BuildConfig
import com.yourapp.petai.data.model.*

class GeminiService {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private fun buildModel(systemPrompt: String): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature    = 0.9f   // kreatifitas (0.0–1.0)
                maxOutputTokens = 100   // biar tetap pendek
                topP           = 0.95f
            },
            systemInstruction = content { text(systemPrompt) }
        )
    }

    /**
     * Kirim pesan user, dapat balasan dari pet.
     */
    suspend fun chat(
        pet: PetPersonality,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<String> {
        return try {
            val model = buildModel(pet.toSystemPrompt())

            val geminiHistory = history.takeLast(10).map { msg ->
                content(role = if (msg.isUser) "user" else "model") {
                    text(msg.text)
                }
            }

            val chat = model.startChat(history = geminiHistory)
            val response = chat.sendMessage(userMessage)
            val reply = response.text?.trim() ?: getFallbackDialog(pet)

            Result.success(reply)

        } catch (e: Exception) {
            Result.success(getFallbackDialog(pet))
        }
    }

    /**
     * Greeting otomatis saat user pertama buka app.
     */
    suspend fun generateGreeting(pet: PetPersonality): Result<String> {
        val prompt = when {
            pet.friendship < 10 ->
                "User baru buka app. Sapa dia dengan sangat malu-malu dan canggung."
            pet.friendship < 50 ->
                "User baru buka app setelah beberapa saat. Sapa dengan hangat tapi tidak berlebihan."
            else ->
                "Sahabatmu baru buka app! Sapa dengan sangat excited dan akrab."
        }
        return chat(pet, emptyList(), prompt)
    }

    // Dialog fallback kalau offline / error
    private fun getFallbackDialog(pet: PetPersonality): String {
        return when (pet.mood) {
            Mood.SHY     -> "A-aku... nggak bisa ngomong sekarang..."
            Mood.MOODY   -> "Hmph."
            Mood.EXCITED -> "Eh eh—!! Ehm, aku lagi susah ngomong hehe..."
            Mood.TIRED   -> "Zzz... hm...?"
            Mood.BESTIE  -> "Bentar ya, aku lagi nggak bisa!"
            else         -> "..."
        }
    }
}
```

---

## 8. ViewModel

### `ui/main/PetViewModel.kt`
```kotlin
package com.yourapp.petai.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yourapp.petai.ai.GeminiService
import com.yourapp.petai.data.engine.FriendshipEngine
import com.yourapp.petai.data.engine.MoodEngine
import com.yourapp.petai.data.model.*
import com.yourapp.petai.data.storage.PetStorage
import kotlinx.coroutines.launch
import java.util.Calendar

class PetViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiService()
    private val chatHistory   = mutableListOf<ChatMessage>()

    val petState    = MutableLiveData<PetPersonality>()
    val dialogText  = MutableLiveData<String>()
    val isLoading   = MutableLiveData(false)
    val milestone   = MutableLiveData<FriendshipMilestone?>()
    val errorEvent  = MutableLiveData<String?>()

    init {
        val pet = PetStorage.load(getApplication())
        if (pet != null) {
            petState.value = pet
            // Cek dan catat daily visit
            handleDailyVisit(pet)
            generateGreeting(pet)
        }
        // Kalau null → Activity harus redirect ke OnboardingActivity
    }

    val isPetReady: Boolean get() = petState.value != null

    /** Dipanggil saat user kirim pesan */
    fun sendMessage(userMessage: String) {
        val pet = petState.value ?: return
        if (userMessage.isBlank()) return

        viewModelScope.launch {
            isLoading.value = true

            chatHistory.add(ChatMessage(userMessage, isUser = true))

            val result = geminiService.chat(pet, chatHistory, userMessage)

            result.onSuccess { reply ->
                chatHistory.add(ChatMessage(reply, isUser = false))
                dialogText.value = reply

                val interactionType = if (userMessage.length > 50)
                    InteractionType.LONG_CHAT
                else
                    InteractionType.CHAT_MESSAGE

                updatePetState(pet, interactionType)
            }

            result.onFailure {
                errorEvent.value = "Gagal konek. Cek internet kamu."
            }

            isLoading.value = false
        }
    }

    private fun generateGreeting(pet: PetPersonality) {
        viewModelScope.launch {
            isLoading.value = true
            val result = geminiService.generateGreeting(pet)
            result.onSuccess { dialogText.value = it }
            isLoading.value = false
        }
    }

    private fun handleDailyVisit(pet: PetPersonality) {
        val lastVisit = PetStorage.getLastVisitTimestamp(getApplication())
        val hoursSince = ((System.currentTimeMillis() - lastVisit) / 3_600_000).toInt()
        if (hoursSince >= 20) {
            // Baru buka lagi setelah lebih dari 20 jam → daily visit bonus
            updatePetState(pet, InteractionType.DAILY_VISIT)
        }
    }

    private fun updatePetState(pet: PetPersonality, type: InteractionType) {
        val oldFriendship = pet.friendship
        var updated = FriendshipEngine.onInteraction(pet, type)

        val ctx = InteractionContext(
            hoursSinceLastVisit = getHoursSinceLastVisit(),
            messagesToday       = chatHistory.count { it.isUser },
            isNightTime         = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) in 20..23,
            userSentiment       = Sentiment.POSITIVE
        )

        updated = updated.copy(mood = MoodEngine.calculateMood(updated, ctx))

        // Cek milestone
        val reached = FriendshipEngine.checkMilestone(oldFriendship, updated.friendship)
        if (reached != null) milestone.value = reached

        PetStorage.save(getApplication(), updated)
        petState.value = updated
    }

    private fun getHoursSinceLastVisit(): Int {
        val last = PetStorage.getLastVisitTimestamp(getApplication())
        return ((System.currentTimeMillis() - last) / 3_600_000).toInt()
    }
}
```

### `ui/onboarding/OnboardingViewModel.kt`
```kotlin
package com.yourapp.petai.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.yourapp.petai.data.model.*
import com.yourapp.petai.data.storage.PetStorage

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    val petName     = MutableLiveData("")
    val archetype   = MutableLiveData(Archetype.SUNNY_KID)
    val energy      = MutableLiveData(5)
    val warmth      = MutableLiveData(5)
    val humor       = MutableLiveData(5)
    val curiosity   = MutableLiveData(5)
    val independence = MutableLiveData(5)

    val isSaved = MutableLiveData(false)

    fun savePet() {
        val name = petName.value?.trim() ?: return
        if (name.isEmpty()) return

        val pet = PetPersonality(
            name         = name,
            archetype    = archetype.value ?: Archetype.SUNNY_KID,
            energy       = energy.value ?: 5,
            warmth       = warmth.value ?: 5,
            humor        = humor.value ?: 5,
            curiosity    = curiosity.value ?: 5,
            independence = independence.value ?: 5,
            friendship   = 0,
            mood         = Mood.SHY
        )

        PetStorage.save(getApplication(), pet)
        isSaved.value = true
    }
}
```

---

## 9. Onboarding Flow

### `ui/onboarding/OnboardingActivity.kt`
```kotlin
package com.yourapp.petai.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yourapp.petai.data.model.Archetype
import com.yourapp.petai.databinding.ActivityOnboardingBinding
import com.yourapp.petai.ui.main.MainActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupArchetypeButtons()
        setupSliders()
        observeViewModel()

        binding.btnSave.setOnClickListener {
            viewModel.petName.value = binding.etPetName.text.toString()
            viewModel.savePet()
        }
    }

    private fun setupArchetypeButtons() {
        // Setiap tombol archetype update viewModel
        binding.btnNight.setOnClickListener {
            viewModel.archetype.value = Archetype.NIGHT_CREATURE
            highlightArchetype(binding.btnNight)
        }
        binding.btnSunny.setOnClickListener {
            viewModel.archetype.value = Archetype.SUNNY_KID
            highlightArchetype(binding.btnSunny)
        }
        binding.btnSea.setOnClickListener {
            viewModel.archetype.value = Archetype.DEEP_SEA
            highlightArchetype(binding.btnSea)
        }
        binding.btnLab.setOnClickListener {
            viewModel.archetype.value = Archetype.LAB_EXPERIMENT
            highlightArchetype(binding.btnLab)
        }
    }

    private fun setupSliders() {
        binding.sliderEnergy.addOnChangeListener { _, value, _ ->
            viewModel.energy.value = value.toInt()
        }
        binding.sliderWarmth.addOnChangeListener { _, value, _ ->
            viewModel.warmth.value = value.toInt()
        }
        binding.sliderHumor.addOnChangeListener { _, value, _ ->
            viewModel.humor.value = value.toInt()
        }
        binding.sliderCuriosity.addOnChangeListener { _, value, _ ->
            viewModel.curiosity.value = value.toInt()
        }
        binding.sliderIndependence.addOnChangeListener { _, value, _ ->
            viewModel.independence.value = value.toInt()
        }
    }

    private fun observeViewModel() {
        viewModel.isSaved.observe(this) { saved ->
            if (saved) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun highlightArchetype(selected: android.widget.Button) {
        // Reset semua
        listOf(
            binding.btnNight, binding.btnSunny,
            binding.btnSea, binding.btnLab
        ).forEach { it.alpha = 0.5f }
        // Highlight yang dipilih
        selected.alpha = 1.0f
    }
}
```

---

## 10. Main Activity — Sambung ke UI

### `ui/main/MainActivity.kt`
```kotlin
package com.yourapp.petai.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.yourapp.petai.databinding.ActivityMainBinding
import com.yourapp.petai.ui.onboarding.OnboardingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Redirect ke onboarding kalau belum setup pet
        if (!viewModel.isPetReady) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        // Dialog pet
        viewModel.dialogText.observe(this) { text ->
            binding.tvDialog.text = text
        }

        // Status bar atas
        viewModel.petState.observe(this) { pet ->
            binding.tvFriendship.text = "❤️ Friendship : ${pet.friendship}"
            binding.tvMood.text = "Mood : ${pet.mood.emoji} ${pet.mood.displayName}"
        }

        // Loading
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.isVisible = loading
            binding.tvDialog.isVisible = !loading
        }

        // Milestone notif
        viewModel.milestone.observe(this) { milestone ->
            milestone?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }

        // Error
        viewModel.errorEvent.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val msg = binding.etInput.text.toString().trim()
            if (msg.isNotEmpty()) {
                viewModel.sendMessage(msg)
                binding.etInput.setText("")
            }
        }
    }
}
```

---

## 11. Flow Lengkap

```
APP DIBUKA
│
├── MainActivity.onCreate()
│   ├── PetStorage.load() → null?
│   │   └── YA → redirect OnboardingActivity
│   │           → user input nama, pilih archetype, atur sliders
│   │           → PetStorage.save() → kembali ke MainActivity
│   │
│   └── ADA DATA → PetViewModel.init()
│                  ├── handleDailyVisit() → friendship bonus kalau baru buka
│                  └── generateGreeting() → Gemini buat sapaan sesuai mood
│
USER KIRIM PESAN
│
├── viewModel.sendMessage(text)
├── Tambah ke chatHistory
├── Kirim ke Gemini (system prompt = personality DNA)
├── Gemini balas sesuai karakter
├── FriendshipEngine.onInteraction() → friendship naik
├── MoodEngine.calculateMood() → mood di-recalculate
├── FriendshipEngine.checkMilestone() → ada milestone? → Toast
├── PetStorage.save() → semua tersimpan
└── UI update: dialog, friendship bar, mood label

TRAIT EVOLUTION (setiap 10 interaksi)
│
├── curiosity + 1 (selalu)
├── warmth + 1 (kalau banyak long chat)
└── independence + 1 (kalau sering diabaikan)
```

---

## 12. Tips & Troubleshooting

### Temperature Gemini
| Nilai | Efek |
|---|---|
| `0.3` | Konsisten, lebih predictable |
| `0.7` | Seimbang (direkomendasikan awal) |
| `0.9` | Kreatif, kadang random |
| `1.0` | Sangat bebas, bisa aneh |

### Common Errors

**`API_KEY_INVALID`**
→ Cek `gradle.properties`, pastikan tidak ada spasi atau newline di key.

**`PERMISSION_DENIED`**
→ Tunggu beberapa menit setelah generate API key baru.

**Response terlalu panjang**
→ Turunkan `maxOutputTokens` atau perkuat aturan di system prompt.

**`NetworkOnMainThreadException`**
→ Pastikan semua call Gemini ada di dalam `viewModelScope.launch {}`.

**`RECITATION` error**
→ Gemini deteksi konten mirip training data. Ubah sedikit phrasing di prompt.

### Best Practices
- Selalu `takeLast(10)` dari history — jangan kirim semua, boros token
- Gunakan `Result<T>` supaya error bisa ditangani dengan rapi
- Test prompt di [AI Studio](https://aistudio.google.com) sebelum implement ke Android
- Simpan API key di `gradle.properties`, masukkan ke `.gitignore`
- Gunakan `viewBinding = true` untuk akses view tanpa `findViewById`

---

> 📄 **Pet AI Android** — Complete Implementation Guide  
> Stack: Kotlin · Gemini 1.5 Flash · Android Studio · MVVM  
> Semua kode siap dieksekusi, tinggal sesuaikan nama package dan layout XML.
