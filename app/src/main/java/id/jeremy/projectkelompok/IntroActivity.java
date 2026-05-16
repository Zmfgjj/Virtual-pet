package id.jeremy.projectkelompok;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class IntroActivity extends AppCompatActivity {

    RelativeLayout bgIntro;
    AppCompatButton btnStart;
    TextView txtStory, btnContinue;

    Handler typingHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        bgIntro = findViewById(R.id.bgIntro);
        btnStart = findViewById(R.id.btnStart);
        txtStory = findViewById(R.id.txtStory);
        btnContinue = findViewById(R.id.btnContinue);

        startPulse(btnStart);

        // 🌟 START BUTTON
        btnStart.setOnClickListener(v -> {

            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        btnStart.animate()
                                .alpha(0f)
                                .scaleX(0.5f)
                                .scaleY(0.5f)
                                .setDuration(400)
                                .withEndAction(() -> btnStart.setVisibility(View.GONE))
                                .start();
                    })
                    .start();

            bgIntro.animate()
                    .scaleX(2.2f)
                    .scaleY(2.2f)
                    .translationX(180f)
                    .translationY(950f)
                    .setDuration(4000)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            new Handler().postDelayed(() -> {
                String storyText =
                        "Di dunia Lunaria,\nspirit hidup sendiri\nmenunggu seseorang...";

                txtStory.setVisibility(View.VISIBLE);
                txtStory.setAlpha(1f);
                animateTypeText(storyText);

            }, 4000);
        });

        // 🌌 CONTINUE → PORTAL EFFECT
        btnContinue.setOnClickListener(v -> {

            txtStory.animate()
                    .alpha(0f)
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(500)
                    .start();

            btnContinue.animate()
                    .alpha(0f)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(500)
                    .start();

            // 🌠 PORTAL WARP
            bgIntro.animate()
                    .scaleX(3f)
                    .scaleY(3f)
                    .rotation(10f)
                    .alpha(0f)
                    .setDuration(900)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            new Handler().postDelayed(() -> {

                Intent intent =
                        new Intent(IntroActivity.this, HouseActivity.class);

                startActivity(intent);

                overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );

                finish();

            }, 800);
        });
    }

    // 🌸 PULSE EFFECT (PORTAL HIDUP)
    private void startPulse(View view) {

        view.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(900)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {

                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(900)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> startPulse(view))
                            .start();

                })
                .start();
    }

    // ✍️ TYPE TEXT
    private void animateTypeText(String text) {

        final int[] charIndex = {0};
        txtStory.setText("");
        btnContinue.setVisibility(View.INVISIBLE);

        typingHandler.removeCallbacksAndMessages(null);

        Runnable typingRunnable = new Runnable() {
            @Override
            public void run() {

                if (charIndex[0] < text.length()) {

                    txtStory.append(
                            String.valueOf(text.charAt(charIndex[0]))
                    );

                    charIndex[0]++;

                    typingHandler.postDelayed(this, 70);
                } else {

                    btnContinue.setVisibility(View.VISIBLE);
                    btnContinue.setAlpha(0f);

                    btnContinue.animate()
                            .alpha(1f)
                            .setDuration(800)
                            .start();
                }
            }
        };

        typingHandler.post(typingRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typingHandler.removeCallbacksAndMessages(null);
    }
}