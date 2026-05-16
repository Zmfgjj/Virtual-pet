package id.jeremy.projectkelompok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class HouseActivity extends AppCompatActivity {

    ImageView imgHouse;
    TextView txtDialog, btnNext;

    Handler blinkHandler = new Handler();
    Handler typingHandler = new Handler();

    private String[] dialogues = {
            "...Ada seseorang di luar?",
            "Sudah lama tidak ada yang datang ke sini...",
            "Aku belum pernah melihatmu sebelumnya.",
            "Siapa namamu...?"
    };

    private int dialogueIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        // 1. INIT VIEW DULU (WAJIB)
        imgHouse = findViewById(R.id.imgHouse);
        txtDialog = findViewById(R.id.txtDialog);
        btnNext = findViewById(R.id.btnNext);

        // 2. SET STATE AWAL (portal feel)
        imgHouse.setAlpha(0f);
        imgHouse.setScaleX(1.1f);
        imgHouse.setScaleY(1.1f);
        imgHouse.setTranslationY(60f);

        // 3. ENTRY ANIMATION (setelah view ready)
        imgHouse.post(() -> {
            imgHouse.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f)
                    .setDuration(700)
                    .start();
        });

        // 4. BARU LOGIC
        showNextDialogue();
        startBlinking();

        btnNext.setOnClickListener(v -> {
            if (dialogueIndex < dialogues.length) {
                showNextDialogue();
            } else {
                showNameInputBottomSheet();
            }
        });
    }

    // 💬 dialog
    private void showNextDialogue() {

        if (dialogueIndex < dialogues.length) {

            String text = dialogues[dialogueIndex];
            dialogueIndex++;

            animateTypeText(text);
        }
    }

    // ⌨️ type effect
    private void animateTypeText(String text) {

        final int[] i = {0};
        txtDialog.setText("");
        btnNext.setVisibility(View.INVISIBLE);

        typingHandler.removeCallbacksAndMessages(null);

        Runnable r = new Runnable() {
            @Override
            public void run() {

                if (i[0] < text.length()) {

                    txtDialog.append(String.valueOf(text.charAt(i[0])));
                    i[0]++;

                    typingHandler.postDelayed(this, 40);

                } else {
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        };

        typingHandler.post(r);
    }

    // 🌸 INPUT NAMA (BOTTOM SHEET)
    private void showNameInputBottomSheet() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_name_input, null);

        dialog.setContentView(view);

        EditText editName = view.findViewById(R.id.editName);
        AppCompatButton btnSave = view.findViewById(R.id.btnSaveName);

        btnSave.setOnClickListener(v -> {

            String name = editName.getText().toString().trim();

            if (name.isEmpty()) {

                Toast.makeText(this, "Masukkan nama dulu ya!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 💾 SIMPAN NAMA (INI YANG PENTING)
            SharedPreferences sp = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            sp.edit().putString("player_name", name).apply();

            dialog.dismiss();

            // 🚀 LANJUT KE MAIN
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        dialog.show();
    }

    // 👁 blink anim
    private void startBlinking() {

        blinkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                imgHouse.setImageResource(R.drawable.bg_blink);

                new Handler().postDelayed(() -> {
                    imgHouse.setImageResource(R.drawable.bg_open);
                }, 150);

                blinkHandler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        typingHandler.removeCallbacksAndMessages(null);
        blinkHandler.removeCallbacksAndMessages(null);
    }
}