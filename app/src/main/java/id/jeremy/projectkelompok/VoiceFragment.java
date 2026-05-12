package id.jeremy.projectkelompok;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceFragment extends Fragment {
    private TextToSpeech tts;
    private TextView tvResult;
    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) startVoiceInput();
        });

    private final ActivityResultLauncher<Intent> voiceLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == -1 && result.getData() != null) {
                ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String text = matches.get(0);
                tvResult.setText(text);
                tts.speak("Kamu bilang " + text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

    public VoiceFragment() { super(R.layout.fragment_voice); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvResult = view.findViewById(R.id.tv_voice_result);
        Button btnMic = view.findViewById(R.id.btn_mic);
        tts = new TextToSpeech(getContext(), status -> tts.setLanguage(Locale.getDefault()));
        btnMic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startVoiceInput();
            } else {
                requestPermission.launch(Manifest.permission.RECORD_AUDIO);
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceLauncher.launch(intent);
    }

    @Override
    public void onDestroy() {
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}