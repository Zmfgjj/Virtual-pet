package id.jeremy.projectkelompok;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private RelativeLayout homeRoot;
    private ImageView imgSpirit;
    private TextView txtDialog, txtFriendship, txtMood;

    private Handler handler = new Handler();
    private boolean blinkRunning = false;

    private String userName = "kamu";
    private int friendship = 0;
    private String mood = "Shy";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // connect xml
        homeRoot = view.findViewById(R.id.homeRoot);
        imgSpirit = view.findViewById(R.id.imgSpirit);
        txtDialog = view.findViewById(R.id.txtDialog);
        txtFriendship = view.findViewById(R.id.txtFriendship);
        txtMood = view.findViewById(R.id.txtMood);

        // Load data
        loadGameData();
        
        // Update Status & Background
        updateInitialState();

        // Mulai intro Spirit dari bawah (Pop-up style)
        startSpiritIntro();

        return view;
    }

    private void loadGameData() {
        SharedPreferences spUser = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userName = spUser.getString("player_name", "kamu");

        SharedPreferences spGame = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
        friendship = spGame.getInt("friendship", 0);
    }

    private void updateInitialState() {
        // Ganti background berdasarkan friendship
        if (friendship < 30) {
            homeRoot.setBackgroundResource(R.drawable.intro1);
            mood = "Shy";
        } else {
            homeRoot.setBackgroundResource(R.drawable.bg_home);
            mood = friendship < 60 ? "Comfortable" : "Attached";
        }

        txtFriendship.setText("❤️ Friendship : " + friendship);
        txtMood.setText("😊 Mood : " + mood);
    }

    private void startSpiritIntro() {
        imgSpirit.post(() -> {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float screenHeight = metrics.heightPixels;

            // Spawn dari bawah layar
            imgSpirit.setTranslationY(screenHeight);
            imgSpirit.setScaleX(2.0f);
            imgSpirit.setScaleY(2.0f);
            imgSpirit.setAlpha(0f);
            imgSpirit.setVisibility(View.VISIBLE);

            // Animasi naik & Settle
            imgSpirit.animate()
                    .translationY(screenHeight * 0.25f)
                    .scaleX(1.7f)
                    .scaleY(1.7f)
                    .alpha(1f)
                    .setDuration(1400)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> {
                        imgSpirit.animate()
                                .translationY(screenHeight * 0.29f)
                                .setDuration(180)
                                .withEndAction(() -> {
                                    animateTypeText("h-halo " + userName + "...");
                                    startBlinkLoop();
                                    startBreathing();
                                })
                                .start();
                    })
                    .start();
        });
    }

    private void animateTypeText(String text) {
        final int[] charIndex = {0};
        txtDialog.setText("");
        handler.removeCallbacksAndMessages(null);
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (charIndex[0] < text.length()) {
                    txtDialog.append(String.valueOf(text.charAt(charIndex[0])));
                    charIndex[0]++;
                    handler.postDelayed(this, 45);
                }
            }
        };
        handler.post(r);
    }

    private void startBlinkLoop() {
        if (blinkRunning) return;
        blinkRunning = true;
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) return;
                imgSpirit.setImageResource(R.drawable.blink_sp);
                new Handler().postDelayed(() -> {
                    if (getActivity() != null) imgSpirit.setImageResource(R.drawable.open_sp);
                }, 120);
                handler.postDelayed(this, 3000 + (int)(Math.random() * 2000));
            }
        }, 3000);
    }

    private void startBreathing() {
        imgSpirit.animate()
                .scaleX(1.75f).scaleY(1.75f)
                .translationYBy(-10f)
                .setDuration(1800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    imgSpirit.animate()
                            .scaleX(1.7f).scaleY(1.7f)
                            .translationYBy(10f)
                            .setDuration(1800)
                            .withEndAction(this::startBreathing)
                            .start();
                }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        blinkRunning = false;
    }
}
