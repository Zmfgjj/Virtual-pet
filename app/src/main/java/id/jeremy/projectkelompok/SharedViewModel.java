package id.jeremy.projectkelompok;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Integer> friendship =
            new MutableLiveData<>(0);

    private final MutableLiveData<String> mood =
            new MutableLiveData<>("Neutral");

    // 🌸 friendship
    public LiveData<Integer> getFriendship() {
        return friendship;
    }

    public void updateFriendship(int amount) {

        Integer current = friendship.getValue();

        if (current == null)
            current = 0;

        friendship.setValue(current + amount);
    }

    // 🌙 mood
    public LiveData<String> getMood() {
        return mood;
    }

    public void setMood(String newMood) {
        mood.setValue(newMood);
    }
}