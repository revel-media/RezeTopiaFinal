package io.krito.com.rezetopia.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.activities.CreateChampion;
import io.krito.com.rezetopia.activities.CreateGroup;
import io.krito.com.rezetopia.activities.Login;
import io.krito.com.rezetopia.activities.Profile;
import io.krito.com.rezetopia.activities.SavedPosts;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.models.pojo.news_feed.NewsFeedItem;
import io.krito.com.rezetopia.receivers.ConnectivityReceiver;
import ru.whalemare.sheetmenu.SheetMenu;

public class SideMenu extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener{

    String userId;

    TextView logoutView;
    TextView myProfileView;
    TextView savedPosts;
    TextView createGroupView;
    TextView champion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu, container, false);

        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        logoutView = view.findViewById(R.id.logoutView);
        myProfileView = view.findViewById(R.id.myProfile);
        savedPosts = view.findViewById(R.id.sideSavedPostsView);
        createGroupView = view.findViewById(R.id.createGroupView);
        champion = view.findViewById(R.id.champion);

        myProfileView.setOnClickListener(this);
        logoutView.setOnClickListener(this);
        savedPosts.setOnClickListener(this);
        createGroupView.setOnClickListener(this);
        champion.setOnClickListener(this);

        return view;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


    @Override
    public void onResume() {
        super.onResume();
        RezetopiaApp.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutView:
                if (ConnectivityReceiver.isConnected(getActivity())){
                    getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .edit().putString(AppConfig.LOGGED_IN_USER_ID_SHARED, null).apply();
                    startActivity(new Intent(getActivity(), Login.class));
                } else {
                    Snackbar.make(v, R.string.connection_error, Snackbar.LENGTH_INDEFINITE).show();
                }
                break;
            case R.id.myProfile:
                Intent intent = Profile.createIntent(userId, getActivity());
                startActivity(intent);
                break;
            case R.id.sideSavedPostsView:
                Intent intent1 = new Intent(getActivity(), SavedPosts.class);
                startActivity(intent1);
                break;
            case R.id.createGroupView:
                Intent intent2 = new Intent(getActivity(), CreateGroup.class);
                startActivity(intent2);
                break;
            case R.id.champion:
                championSheetMenu();
                break;
        }
    }

    private void championSheetMenu() {
        SheetMenu.with(getActivity())
                .setMenu(R.menu.champion_menu)
                .setAutoCancel(true)
                .setClick(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem it) {
                        switch (it.getItemId()) {
                            case R.id.champion_menu:
                                break;
                            case R.id.today_matches_menu:
                                break;
                            case R.id.create_champion_menu:
                                startActivity(new Intent(getActivity(), CreateChampion.class));
                                break;
                        }
                        return false;
                    }
                }).show();
    }
}
