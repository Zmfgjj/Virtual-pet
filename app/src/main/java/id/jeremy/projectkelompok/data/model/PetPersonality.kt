package id.jeremy.projectkelompok.data.model

data class PetPersonality(
    val name: String,
    val archetype: Archetype,
    val energy: Int,
    val warmth: Int,
    val humor: Int,
    val curiosity: Int,
    val independence: Int,
    var friendship: Int = 0,
    var mood: Mood = Mood.SHY,
    var totalInteractions: Int = 0
) {
    fun toSystemPrompt(): String {
        val energyDesc = if (energy > 7) "kamu antusias dan ekspresif, sering pakai tanda seru" else "kamu kalem dan santai, bicara pelan-pelan"
        val warmthDesc = if (warmth > 7) "kamu hangat dan perhatian ke user" else "kamu agak reserved, tidak terlalu ekspresif"
        val humorDesc = if (humor > 6) "kamu suka lempar joke dan plesetan" else "kamu lebih serius dan to-the-point"
        val curiosityDesc = if (curiosity > 6) "kamu selalu balik nanya sesuatu di akhir kalimat" else "kamu lebih banyak dengerin"
        val independenceDesc = if (independence < 4) "kamu clingy, butuh perhatian user" else "kamu oke kalau sendirian"
        val friendshipDesc = when {
            friendship < 10 -> "Kamu baru kenal user, masih sangat malu dan ragu-ragu"
            friendship < 30 -> "Kamu mulai nyaman, tapi masih agak canggung"
            friendship < 60 -> "Kamu sudah cukup dekat, mulai santai dan terbuka"
            friendship < 85 -> "Kamu sudah dekat banget, sering bercanda"
            else -> "User adalah sahabat terbaikmu, kamu sangat nyaman"
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

enum class Archetype(val displayName: String, val description: String, val emoji: String) {
    NIGHT_CREATURE("Makhluk Malam", "misterius, suka filosofi, aktif dan lebih hidup di malam hari", "🌙"),
    SUNNY_KID("Anak Padang", "optimis, blak-blakan, ceria dan penuh semangat", "☀️"),
    DEEP_SEA("Dari Laut Dalam", "tenang, pendiam, suka cerita panjang dan hal misterius", "🌊"),
    LAB_EXPERIMENT("Eksperimen Lab", "sangat curious, kadang random dan glitchy, suka hal aneh", "⚡")
}

enum class Mood(val displayName: String, val emoji: String) {
    SHY("Shy", "😶"), WARMING_UP("Warming Up", "🌱"), FRIENDLY("Friendly", "😊"),
    PLAYFUL("Playful", "😝"), MOODY("Moody", "😒"), EXCITED("Excited", "🤩"),
    TIRED("Tired", "😴"), BESTIE("Bestie Mode", "🥰")
}
