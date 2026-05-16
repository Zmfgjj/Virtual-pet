package id.jeremy.projectkelompok;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private ImageView imgSpirit;

    private TextView txtDialog;
    private TextView txtFriendship;
    private TextView txtMood;

    private Handler handler = new Handler();

    private boolean blinkRunning = false;

    // 🌸 PLAYER DATA
    private String userName = "kamu";

    // ❤️ GAME SYSTEM
    private int friendship = 0;
    private String mood = "Shy";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // connect xml
        imgSpirit = view.findViewById(R.id.imgSpirit);

        txtDialog = view.findViewById(R.id.txtDialog);

        txtFriendship = view.findViewById(R.id.txtFriendship);
        txtMood = view.findViewById(R.id.txtMood);

        // ambil nama user
        loadUserName();

        // ambil friendship tersimpan
        loadFriendship();

        // update status awal
        updateStatus();

        // mulai intro spirit
        startSpiritIntro();

        return view;
    }

    // 💾 AMBIL NAMA USER
    private void loadUserName() {

        SharedPreferences sp =
                requireActivity().getSharedPreferences("UserPrefs", 0);

        userName = sp.getString("player_name", "kamu");
    }

    // ❤️ LOAD FRIENDSHIP
    private void loadFriendship() {

        SharedPreferences sp =
                requireActivity().getSharedPreferences("GameData", 0);

        friendship = sp.getInt("friendship", 0);
    }

    // ❤️ SAVE FRIENDSHIP
    private void saveFriendship() {

        SharedPreferences sp =
                requireActivity().getSharedPreferences("GameData", 0);

        sp.edit()
                .putInt("friendship", friendship)
                .apply();
    }

    // ❤️ TAMBAH FRIENDSHIP
    public void addFriendship(int amount) {

        friendship += amount;

        if (friendship > 100)
            friendship = 100;

        saveFriendship();

        updateStatus();

        // 🌸 SPECIAL EVENT
        if (friendship >= 30) {

            animateText(
                    "aku... mulai nyaman sama kamu..."
            );
        }
    }

    // ❤️ UPDATE STATUS
    private void updateStatus() {

        // mood berdasarkan friendship
        if (friendship < 10) {

            mood = "Shy";

        } else if (friendship < 30) {

            mood = "Comfortable";

        } else {

            mood = "Attached";
        }

        txtFriendship.setText("❤ Friendship : " + friendship);
        txtMood.setText("Mood : " + mood);
    }

    // 🌙 INTRO SPIRIT
    private void startSpiritIntro() {

        imgSpirit.post(() -> {

            DisplayMetrics metrics =
                    getResources().getDisplayMetrics();

            float screenHeight = metrics.heightPixels;

            // spawn dari bawah layar
            imgSpirit.setTranslationY(screenHeight);

            // zoom awal
            imgSpirit.setScaleX(2.0f);
            imgSpirit.setScaleY(2.0f);

            // fade awal
            imgSpirit.setAlpha(0f);

            imgSpirit.setVisibility(View.VISIBLE);

            // animasi naik
            imgSpirit.animate()
                    .translationY(screenHeight * 0.25f)
                    .scaleX(1.7f)
                    .scaleY(1.7f)
                    .alpha(1f)
                    .setDuration(1400)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> {

                        // settle kecil
                        imgSpirit.animate()
                                .translationY(screenHeight * 0.29f)
                                .setDuration(180)
                                .withEndAction(() -> {

                                    animateText(
                                            "h-halo " + userName + "..."
                                    );

                                    startBlinkLoop();
                                    startBreathing();

                                })
                                .start();

                    })
                    .start();
        });
    }

    // 💬 TYPEWRITER
    private void animateText(String text) {

        final int[] i = {0};

        txtDialog.setText("");

        Runnable r = new Runnable() {
            @Override
            public void run() {

                if (i[0] < text.length()) {

                    txtDialog.append(
                            String.valueOf(text.charAt(i[0]))
                    );

                    i[0]++;

                    handler.postDelayed(this, 45);
                }
            }
        };

        handler.post(r);
    }

    // 👁 BLINK LOOP
    private void startBlinkLoop() {

        if (blinkRunning) return;

        blinkRunning = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (getActivity() == null) return;

                imgSpirit.setImageResource(R.drawable.blink_sp);

                new Handler().postDelayed(() -> {

                    imgSpirit.setImageResource(R.drawable.open_sp);

                }, 120);

                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    // 🌸 BREATHING ANIMATION
    private void startBreathing() {

        Runnable breathe = new Runnable() {
            @Override
            public void run() {

                imgSpirit.animate()
                        .scaleX(1.75f)
                        .scaleY(1.75f)
                        .translationYBy(-10f)
                        .setDuration(1800)
                        .setInterpolator(
                                new AccelerateDecelerateInterpolator()
                        )
                        .withEndAction(() -> {

                            imgSpirit.animate()
                                    .scaleX(1.7f)
                                    .scaleY(1.7f)
                                    .translationYBy(10f)
                                    .setDuration(1800)
                                    .setInterpolator(
                                            new AccelerateDecelerateInterpolator()
                                    )
                                    .withEndAction(this)
                                    .start();

                        })
                        .start();
            }
        };

        handler.post(breathe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        handler.removeCallbacksAndMessages(null);

        blinkRunning = false;
    }
}