package wycliffeassociates.recordingapp.Playback.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wycliffeassociates.recordingapp.R;

/**
 * Created by sarabiaj on 11/4/2016.
 */

public class FragmentPlaybackTools extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_player_toolbar, container, false);
    }
}
