package id.jeremy.projectkelompok;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        
        TextView tvMood = view.findViewById(R.id.tv_mood);
        ProgressBar pbFriendship = view.findViewById(R.id.pb_friendship);

        viewModel.getMood().observe(getViewLifecycleOwner(), mood -> tvMood.setText("Mood: " + mood));
        viewModel.getFriendship().observe(getViewLifecycleOwner(), pbFriendship::setProgress);

        view.findViewById(R.id.btn_feed).setOnClickListener(v -> viewModel.feedSpirit());

        return view;
    }
}