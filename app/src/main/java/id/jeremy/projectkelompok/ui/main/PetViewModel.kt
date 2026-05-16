package id.jeremy.projectkelompok.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.jeremy.projectkelompok.ai.GeminiService
import id.jeremy.projectkelompok.data.engine.FriendshipEngine
import id.jeremy.projectkelompok.data.engine.MoodEngine
import id.jeremy.projectkelompok.data.model.*
import id.jeremy.projectkelompok.data.storage.PetStorage
import kotlinx.coroutines.launch
import java.util.Calendar

class PetViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiService = GeminiService()
    private val chatHistory   = mutableListOf<ChatMessage>()

    val petState    = MutableLiveData<PetPersonality>()
    val dialogText  = MutableLiveData<String>()
    val isLoading   = MutableLiveData(false)
    val milestone   = MutableLiveData<id.jeremy.projectkelompok.data.engine.FriendshipMilestone?>()
    val errorEvent  = MutableLiveData<String?>()

    init {
        val pet = PetStorage.load(getApplication())
        if (pet != null) {
            petState.value = pet
            handleDailyVisit(pet)
            generateGreeting(pet)
        }
    }

    val isPetReady: Boolean get() = petState.value != null

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
