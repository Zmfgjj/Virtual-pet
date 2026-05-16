package id.jeremy.projectkelompok;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class CameraFragment extends Fragment {

    private ImageView imageView;
    private ImageView imgSpiritCamera;
    private TextView txtReaction;

    private Handler handler = new Handler();
    private Handler blinkHandler = new Handler();

    public CameraFragment() {
        super(R.layout.fragment_camera);
    }

    // 📷 CAMERA RESULT
    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicturePreview(),
                    bitmap -> {

                        if (bitmap != null) {

                            imageView.setImageBitmap(bitmap);

                            // 🌙 spirit react happy
                            imgSpiritCamera.setImageResource(R.drawable.happy_sp);

                            new Handler().postDelayed(() -> {
                                imgSpiritCamera.setImageResource(R.drawable.open_sp);
                            }, 1200);

                            spiritReaction();
                        }
                    });

    // 🔐 PERMISSION
    private final ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            cameraLauncher.launch(null);
                        }
                    });

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.img_captured);
        imgSpiritCamera = view.findViewById(R.id.imgSpiritCamera);
        txtReaction = view.findViewById(R.id.txtReaction);

        Button btnCamera = view.findViewById(R.id.btn_capture);

        // 📷 BUTTON
        btnCamera.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {

                cameraLauncher.launch(null);

            } else {
                requestPermission.launch(Manifest.permission.CAMERA);
            }
        });

        // 🌙 START SPIRIT LIFE ANIMATION
        startSpiritLife();
    }

    // 🌸 SPIRIT LIFE (BLINK + BREATHING LOOP)
    private void startSpiritLife() {

        startBlink();
        startBreathing();
    }

    // 👁 BLINK
    private void startBlink() {

        blinkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (getActivity() == null) return;

                imgSpiritCamera.setImageResource(R.drawable.blink_sp);

                new Handler().postDelayed(() -> {
                    imgSpiritCamera.setImageResource(R.drawable.open_sp);
                }, 120);

                blinkHandler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    // 🌸 BREATHING (INI YANG KAMU TANYA)
    private void startBreathing() {

        imgSpiritCamera.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(2000)
                .withEndAction(() -> {

                    imgSpiritCamera.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(2000)
                            .withEndAction(this::startBreathing)
                            .start();

                })
                .start();
    }

    // 💬 REACTION SYSTEM
    private void spiritReaction() {

        String[] reactions = {
                "Aku melihat duniamu… 🌙",
                "Tempat itu terasa hangat…",
                "Aku ingin ikut ke sana…",
                "Foto ini terasa hidup…",
                "Kamu menunjukkan dunia padaku…"
        };

        String response =
                reactions[new Random().nextInt(reactions.length)];

        txtReaction.setText("");

        final int[] i = {0};

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (i[0] < response.length()) {

                    txtReaction.append(
                            String.valueOf(response.charAt(i[0]))
                    );

                    i[0]++;

                    handler.postDelayed(this, 30);

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        handler.removeCallbacksAndMessages(null);
        blinkHandler.removeCallbacksAndMessages(null);
    }
}