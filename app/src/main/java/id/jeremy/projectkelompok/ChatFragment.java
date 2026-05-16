package id.jeremy.projectkelompok;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {

    private TextView tvHistory;
    private EditText etMessage;
    private ImageView btnSend;

    public ChatFragment() {
        super(R.layout.fragment_chat);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        tvHistory = view.findViewById(R.id.tv_chat_history);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);

        // 🌸 greeting awal
        tvHistory.setText(
                "🌸 Spirit:\n" +
                        "h-halo... aku senang kamu datang lagi 🌙\n"
        );

        btnSend.setOnClickListener(v -> {

            String userMsg =
                    etMessage.getText()
                            .toString()
                            .trim();

            // kalau kosong
            if (TextUtils.isEmpty(userMsg)) {
                return;
            }

            String lowerMsg = userMsg.toLowerCase();

            String response;

            int friendshipAdd = 1;

            String mood = "Shy";

            // 🌧 sedih
            if (lowerMsg.contains("sedih")
                    || lowerMsg.contains("capek")
                    || lowerMsg.contains("lelah")) {

                response =
                        "Aku di sini buat nemenin kamu 🌙";

                friendshipAdd = 5;
                mood = "Comforting";
            }

            // 🌸 halo
            else if (lowerMsg.contains("halo")
                    || lowerMsg.contains("hai")) {

                response =
                        "Halo... aku senang kamu datang.";

                friendshipAdd = 2;
                mood = "Happy";
            }

            // 💖 sayang
            else if (lowerMsg.contains("sayang")
                    || lowerMsg.contains("kangen")) {

                response =
                        "A-aku juga senang bersamamu...";

                friendshipAdd = 8;
                mood = "Blushing";
            }

            // ⭐ default
            else {

                response =
                        "Cerita lagi dong... aku dengerin.";

                friendshipAdd = 1;
            }

            // ❤️ SharedPreferences
            SharedPreferences sp =
                    requireActivity()
                            .getSharedPreferences("GameData", 0);

            int friendship =
                    sp.getInt("friendship", 0);

            // ❤️ tambah friendship
            friendship += friendshipAdd;

            if (friendship > 100) {
                friendship = 100;
            }

            // 💾 save
            sp.edit()
                    .putInt("friendship", friendship)
                    .putString("mood", mood)
                    .apply();

            // 🌙 tampilkan chat
            tvHistory.append(

                    "\n🧍 Kamu:\n" +
                            userMsg +

                            "\n\n🌸 Spirit:\n" +
                            response +

                            "\n"
            );

            // 🌸 special event
            if (friendship >= 30) {

                tvHistory.append(
                        "\n✨ Spirit:\n" +
                                "aku... mulai nyaman sama kamu...\n"
                );
            }

            // clear input
            etMessage.setText("");
        });
    }
}