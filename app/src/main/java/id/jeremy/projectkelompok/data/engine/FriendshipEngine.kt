package id.jeremy.projectkelompok.data.engine

import id.jeremy.projectkelompok.data.model.*

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
            curiosity = (pet.curiosity + 1).coerceAtMost(10),
            warmth = if (type == InteractionType.LONG_CHAT) (pet.warmth + 1).coerceAtMost(10) else pet.warmth,
            independence = if (type == InteractionType.IGNORED_PET) (pet.independence + 1).coerceAtMost(10) else pet.independence
        )
    }

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
