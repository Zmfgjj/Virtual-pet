package id.jeremy.projectkelompok.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import id.jeremy.projectkelompok.data.model.Archetype
import id.jeremy.projectkelompok.databinding.ActivityOnboardingBinding
import id.jeremy.projectkelompok.MainActivity

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
        binding.btnNight.setOnClickListener { viewModel.archetype.value = Archetype.NIGHT_CREATURE; highlightArchetype(binding.btnNight) }
        binding.btnSunny.setOnClickListener { viewModel.archetype.value = Archetype.SUNNY_KID; highlightArchetype(binding.btnSunny) }
        binding.btnSea.setOnClickListener { viewModel.archetype.value = Archetype.DEEP_SEA; highlightArchetype(binding.btnSea) }
        binding.btnLab.setOnClickListener { viewModel.archetype.value = Archetype.LAB_EXPERIMENT; highlightArchetype(binding.btnLab) }
    }

    private fun setupSliders() {
        binding.sliderEnergy.addOnChangeListener { _, value, _ -> viewModel.energy.value = value.toInt() }
        binding.sliderWarmth.addOnChangeListener { _, value, _ -> viewModel.warmth.value = value.toInt() }
        binding.sliderHumor.addOnChangeListener { _, value, _ -> viewModel.humor.value = value.toInt() }
        binding.sliderCuriosity.addOnChangeListener { _, value, _ -> viewModel.curiosity.value = value.toInt() }
        binding.sliderIndependence.addOnChangeListener { _, value, _ -> viewModel.independence.value = value.toInt() }
    }

    private fun observeViewModel() {
        viewModel.isSaved.observe(this) { saved ->
            if (saved) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun highlightArchetype(selected: Button) {
        listOf(binding.btnNight, binding.btnSunny, binding.btnSea, binding.btnLab).forEach { it.alpha = 0.5f }
        selected.alpha = 1.0f
    }
}
