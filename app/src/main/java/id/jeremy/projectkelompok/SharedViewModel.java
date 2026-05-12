package id.jeremy.projectkelompok;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> mood = new MutableLiveData<>("Happy");
    private final MutableLiveData<Integer> friendship = new MutableLiveData<>(50);

    public LiveData<String> getMood() { return mood; }
    public void setMood(String newMood) { mood.setValue(newMood); }
    public LiveData<Integer> getFriendship() { return friendship; }
    public void updateFriendship(int increment) {
        int current = friendship.getValue() != null ? friendship.getValue() : 0;
        friendship.setValue(current + increment);
    }
}