package id.jeremy.projectkelompok;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {

            NavController navController =
                    navHostFragment.getNavController();

            ImageView btnHome = findViewById(R.id.btnNavHome);
            ImageView btnChat = findViewById(R.id.btnNavChat);
            ImageView btnVoice = findViewById(R.id.btnNavVoice);
            ImageView btnCamera = findViewById(R.id.btnNavCamera);
            ImageView btnSettings = findViewById(R.id.btnNavSettings);

            // NAVIGATION
            btnHome.setOnClickListener(v ->
                    navController.navigate(R.id.navigation_home));

            btnChat.setOnClickListener(v ->
                    navController.navigate(R.id.navigation_chat));

            btnVoice.setOnClickListener(v ->
                    navController.navigate(R.id.navigation_voice));

            btnCamera.setOnClickListener(v ->
                    navController.navigate(R.id.navigation_camera));

            btnSettings.setOnClickListener(v ->
                    navController.navigate(R.id.navigation_settings));

            // ✨ ANIMASI HIDUP
            animateFloatingIcon(btnHome);
            animateFloatingIcon(btnChat);
            animateFloatingIcon(btnVoice);
            animateFloatingIcon(btnCamera);
            animateFloatingIcon(btnSettings);
        }
    }

    private void animateFloatingIcon(View view) {

        // FLOAT (naik turun)
        ObjectAnimator floatAnim =
                ObjectAnimator.ofFloat(view, "translationY", 0f, -12f, 0f);

        floatAnim.setDuration(2200);
        floatAnim.setRepeatCount(ObjectAnimator.INFINITE);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.start();

        // BREATHING SCALE X
        ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.06f, 1f);

        scaleX.setDuration(2000);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();

        // BREATHING SCALE Y
        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.06f, 1f);

        scaleY.setDuration(2000);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.start();
    }
}