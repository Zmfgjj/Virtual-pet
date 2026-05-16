package id.jeremy.projectkelompok.data.model

data class InteractionContext(
    val hoursSinceLastVisit: Int,
    val messagesToday: Int,
    val isNightTime: Boolean,
    val userSentiment: Sentiment
)

enum class Sentiment { POSITIVE, NEUTRAL, NEGATIVE }

enum class InteractionType {
    CHAT_MESSAGE, LONG_CHAT, DAILY_VISIT, IGNORED_PET
}
