package id.jeremy.projectkelompok.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import id.jeremy.projectkelompok.BuildConfig
import id.jeremy.projectkelompok.data.model.*

class GeminiService {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private fun buildModel(systemPrompt: String): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature    = 0.9f
                maxOutputTokens = 100
                topP           = 0.95f
            },
            systemInstruction = content { text(systemPrompt) }
        )
    }

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
            e.printStackTrace() // Tambahkan ini untuk melihat errornya di Logcat
            Result.success(getFallbackDialog(pet))
        }
    }

    suspend fun generateGreeting(pet: PetPersonality): Result<String> {
        val prompt = when {
            pet.friendship < 10 -> "User baru buka app. Sapa dia dengan sangat malu-malu dan canggung."
            pet.friendship < 50 -> "User baru buka app setelah beberapa saat. Sapa dengan hangat tapi tidak berlebihan."
            else -> "Sahabatmu baru buka app! Sapa dengan sangat excited dan akrab."
        }
        return chat(pet, emptyList(), prompt)
    }

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
