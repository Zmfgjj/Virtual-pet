package id.jeremy.projectkelompok;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class VoiceFragment extends Fragment {

    private TextView tvResult, tvSpiritResponse;
    private ImageView btnMic, imgSpiritVoice;

    private TextToSpeech tts;

    public VoiceFragment() {
        super(R.layout.fragment_voice);
    }

    private final ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) startVoiceInput();
                    });

    private final ActivityResultLauncher<Intent> voiceLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getData() == null) return;

                        ArrayList<String> matches =
                                result.getData().getStringArrayListExtra(
                                        RecognizerIntent.EXTRA_RESULTS
                                );

                        if (matches == null || matches.isEmpty()) return;

                        String text = matches.get(0);

                        tvResult.setText("Kamu: " + text);

                        processSpiritResponse(text);
                    });

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        tvResult = view.findViewById(R.id.tv_voice_result);
        tvSpiritResponse = view.findViewById(R.id.tv_spirit_response);
        btnMic = view.findViewById(R.id.btn_mic);
        imgSpiritVoice = view.findViewById(R.id.imgSpiritVoice);

        tts = new TextToSpeech(getContext(),
                status -> tts.setLanguage(Locale.getDefault()));

        btnMic.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED) {

                startVoiceInput();

            } else {
                requestPermission.launch(Manifest.permission.RECORD_AUDIO);
            }
        });
    }

    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        voiceLauncher.launch(intent);
    }

    private void processSpiritResponse(String text) {

        String lower = text.toLowerCase();

        String response;

        if (lower.contains("halo")) {
            response = "halo... aku mendengarmu";
        } else if (lower.contains("sedih")) {
            response = "aku di sini untukmu...";
        } else {
            response = "aku mendengarkan...";
        }

        tvSpiritResponse.setText("Spirit: " + response);

        tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onDestroy() {
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}