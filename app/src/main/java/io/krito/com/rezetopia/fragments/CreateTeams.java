package io.krito.com.rezetopia.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.krito.com.rezetopia.R;

/**
 * Created by Ahmed Ali on 7/21/2018.
 */

public class CreateTeams extends DialogFragment{

    LinearLayout parentLayout;

    int numOfPlayers;

    public static CreateTeams createFragment(String alert){
        Bundle bundle = new Bundle();
        bundle.putString("players", alert);
        CreateTeams fragment = new CreateTeams();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_team_fragment, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        numOfPlayers = getArguments().getInt("alert");



        return view;
    }
}
