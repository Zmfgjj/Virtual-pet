package id.jeremy.projectkelompok;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class IntroActivity extends AppCompatActivity {

    RelativeLayout bgIntro;
    AppCompatButton btnStart;
    TextView txtStory, btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // connect xml ke java
        bgIntro = findViewById(R.id.bgIntro);
        btnStart = findViewById(R.id.btnStart);
        txtStory = findViewById(R.id.txtStory);
        btnContinue = findViewById(R.id.btnContinue);

        // tombol start diklik
        btnStart.setOnClickListener(v -> {

            // tombol start hilang
            btnStart.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .start();

            // kamera zoom ke langit
            bgIntro.animate()
                    .scaleX(2.2f)
                    .scaleY(2.2f)
                    .translationX(180f)
                    .translationY(950f)
                    .setDuration(4000)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            // setelah zoom selesai
            new Handler().postDelayed(() -> {

                // munculkan story
                txtStory.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.VISIBLE);

                // fade in story
                txtStory.setAlpha(0f);
                btnContinue.setAlpha(0f);

                txtStory.animate()
                        .alpha(1f)
                        .setDuration(1500)
                        .start();

                btnContinue.animate()
                        .alpha(1f)
                        .setDuration(1500)
                        .start();

            }, 4000);

        });

        // tombol continue diklik
        btnContinue.setOnClickListener(v -> {

            // story menghilang
            txtStory.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .start();

            btnContinue.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .start();

            // pindah ke halaman rumah
            new Handler().postDelayed(() -> {

                Intent intent = new Intent(IntroActivity.this, HouseActivity.class);
                startActivity(intent);

            }, 1000);

        });

    }
}