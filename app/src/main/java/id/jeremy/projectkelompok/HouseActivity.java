package id.jeremy.projectkelompok;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class HouseActivity extends AppCompatActivity {

    ImageView imgSpirit;
    TextView txtDialog, btnNext;

    Handler blinkHandler = new Handler();

    // Dialog sequence
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

        // connect xml ke java
        imgSpirit = findViewById(R.id.imgSpirit);
        txtDialog = findViewById(R.id.txtDialog);
        btnNext = findViewById(R.id.btnNext);

        // Initial state: Sembunyikan spirit dan dialog
        imgSpirit.setAlpha(0f);
        imgSpirit.setTranslationX(-50f); 
        txtDialog.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);

        // Jalankan sequence: Scene muncul -> Spirit ngintip -> Dialog
        new Handler().postDelayed(() -> {
            // Spirit ngintip (fade in + slide)
            imgSpirit.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(1500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        // Setelah ngintip, munculkan dialog pertama
                        showNextDialogue();
                        // Mulai kedip setelah muncul
                        startBlinking();
                    })
                    .start();
        }, 1000);

        // Klik continue
        btnNext.setOnClickListener(v -> {
            if (dialogueIndex < dialogues.length) {
                showNextDialogue();
            } else {
                // Selesai dialog, munculkan input nama dari bawah
                showNameInputBottomSheet();
            }
        });

    }

    private void showNextDialogue() {
        if (dialogueIndex < dialogues.length) {
            // Animasi text fade out
            txtDialog.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        // Ganti text
                        txtDialog.setText(dialogues[dialogueIndex]);
                        dialogueIndex++;

                        // Tampilkan kembali text
                        txtDialog.setVisibility(View.VISIBLE);
                        txtDialog.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .withEndAction(() -> {
                                    // Tampilkan tombol next jika belum muncul
                                    if (btnNext.getVisibility() != View.VISIBLE) {
                                        btnNext.setVisibility(View.VISIBLE);
                                        btnNext.setAlpha(0f);
                                        btnNext.animate().alpha(1f).setDuration(500).start();
                                    }
                                })
                                .start();
                    })
                    .start();
        }
    }

    private void showNameInputBottomSheet() {
        // Menggunakan BottomSheetDialog agar muncul dari bawah
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_name_input, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false); // Player wajib isi nama

        EditText editName = view.findViewById(R.id.editName);
        AppCompatButton btnSaveName = view.findViewById(R.id.btnSaveName);

        btnSaveName.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (!name.isEmpty()) {
                // Simpan ke SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("player_name", name);
                editor.apply();

                bottomSheetDialog.dismiss();

                // Animasi Spirit merespon dengan nama baru
                txtDialog.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            txtDialog.setText("Salam kenal, " + name + "!");
                            txtDialog.animate().alpha(1f).setDuration(500).start();
                            btnNext.setVisibility(View.INVISIBLE); 
                        })
                        .start();
                
            } else {
                Toast.makeText(this, "Tolong masukkan namamu dulu ya!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void startBlinking(){
        blinkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // mata kedip
                imgSpirit.setImageResource(R.drawable.spirite_blink);

                // balik normal
                new Handler().postDelayed(() -> {
                    imgSpirit.setImageResource(R.drawable.spirite_open);
                }, 200);

                // ulang terus
                blinkHandler.postDelayed(this, 3000);
            }
        }, 3000);
    }
}
