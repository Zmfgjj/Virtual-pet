package id.jeremy.projectkelompok.data.engine

import id.jeremy.projectkelompok.data.model.*

object MoodEngine {

    fun calculateMood(pet: PetPersonality, context: InteractionContext): Mood {
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
            ctx.hoursSinceLastVisit > 24 && pet.independence < 4 -> Mood.MOODY
            ctx.isNightTime && pet.archetype == Archetype.NIGHT_CREATURE -> Mood.EXCITED
            ctx.messagesToday > 20 && pet.energy < 4 -> Mood.TIRED
            ctx.hoursSinceLastVisit > 12 && pet.warmth > 7 -> Mood.EXCITED
            ctx.userSentiment == Sentiment.NEGATIVE && pet.independence < 5 -> Mood.MOODY
            else -> base
        }
    }
}
