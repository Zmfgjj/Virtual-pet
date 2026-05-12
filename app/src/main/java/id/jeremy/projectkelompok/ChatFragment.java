package id.jeremy.projectkelompok;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ChatFragment extends Fragment {
    public ChatFragment() { super(R.layout.fragment_chat); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        TextView tvHistory = view.findViewById(R.id.tv_chat_history);
        EditText etMessage = view.findViewById(R.id.et_message);
        Button btnSend = view.findViewById(R.id.btn_send);

        btnSend.setOnClickListener(v -> {
            String userMsg = etMessage.getText().toString().toLowerCase();
            String response;
            
            if (userMsg.contains("sedih")) {
                response = "Aku di sini buat nemenin kamu 🌙";
                viewModel.setMood("Happy");
                viewModel.updateFriendship(5);
            } else if (userMsg.contains("halo")) {
                response = "Halo... aku senang kamu datang.";
            } else {
                response = "Cerita lagi dong, aku dengerin.";
            }
            
            tvHistory.append("\nUser: " + userMsg + "\nSpirit: " + response);
            etMessage.setText("");
        });
    }
}