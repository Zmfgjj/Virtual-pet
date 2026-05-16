package id.jeremy.projectkelompok;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import id.jeremy.projectkelompok.ui.main.PetViewModel;

public class ChatFragment extends Fragment {

    private TextView tvHistory;
    private EditText etMessage;
    private ImageView btnSend;
    private PetViewModel viewModel;

    public ChatFragment() {
        super(R.layout.fragment_chat);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHistory = view.findViewById(R.id.tv_chat_history);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PetViewModel.class);

        // Observe responses from Gemini AI
        viewModel.getDialogText().observe(getViewLifecycleOwner(), reply -> {
            tvHistory.append("\n\n🌸 Spirit:\n" + reply + "\n");
        });

        // Observe milestones
        viewModel.getMilestone().observe(getViewLifecycleOwner(), milestone -> {
            if (milestone != null) {
                tvHistory.append("\n✨ Milestone tercapai: " + milestone.toString() + " ✨\n");
            }
        });

        btnSend.setOnClickListener(v -> {
            String userMsg = etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(userMsg)) {
                return;
            }

            // Show user message in UI
            tvHistory.append("\n\n🧍 Kamu:\n" + userMsg);

            // Send to Gemini via ViewModel
            viewModel.sendMessage(userMsg);

            // Clear input
            etMessage.setText("");
        });
    }
}
