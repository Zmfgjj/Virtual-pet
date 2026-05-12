package id.jeremy.projectkelompok;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class CameraFragment extends Fragment {
    private ImageView imageView;
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap != null) imageView.setImageBitmap(bitmap);
        });

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) cameraLauncher.launch(null);
        });

    public CameraFragment() { super(R.layout.fragment_camera); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageView = view.findViewById(R.id.img_captured);
        Button btnCamera = view.findViewById(R.id.btn_capture);
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null);
            } else {
                requestPermission.launch(Manifest.permission.CAMERA);
            }
        });
    }
}