package id.jeremy.projectkelompok.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import id.jeremy.projectkelompok.data.model.*
import id.jeremy.projectkelompok.data.storage.PetStorage

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
