package id.jeremy.projectkelompok.data.storage

import android.content.Context
import id.jeremy.projectkelompok.data.model.*

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
        val name = prefs.getString("name", null) ?: return null

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
            null
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
