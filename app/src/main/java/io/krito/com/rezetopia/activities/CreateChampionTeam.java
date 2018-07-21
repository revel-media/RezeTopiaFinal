package io.krito.com.rezetopia.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.helper.VolleyMultipartRequest;

public class CreateChampionTeam extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 1006;
    private static final String NUMBER_OF_TEAMS = "num_of_teams";
    private static final String NUMBER_OF_PLAYERS = "num_of_teams";

    RecyclerView recView;
    Button createTeams;

    int numOfTeams;
    int numOfPlayers;
    String[] teamNames;
    String[] teamImages;
    boolean[] teamNameErrors;
    HashMap<Integer, String[]> players;
    HashMap<Integer, Boolean[]> playerErrors;
    List<String> selectedImages;
    int nowPosition;
    TeamsRecyclerAdapter adapter;
    Bitmap[] tmpImg;
    int championId;
    

    public static Intent createIntent(int n_teams, int n_players, Context context){
        Intent intent = new Intent(context, CreateChampionTeam.class);
        intent.putExtra(NUMBER_OF_PLAYERS, n_players);
        intent.putExtra(NUMBER_OF_TEAMS, n_teams);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_champion_team);

        if (getIntent().getExtras() != null){
            numOfPlayers = getIntent().getExtras().getInt("players");
            numOfTeams = getIntent().getExtras().getInt("teams");
            championId = getIntent().getExtras().getInt("championId");

            teamNames = new String[numOfTeams];
            teamImages = new String[numOfTeams];
            teamNameErrors = new boolean[numOfTeams];
            for (int j = 0; j < teamNameErrors.length; j++){
                teamNameErrors[j] = true;
            }
            tmpImg = new Bitmap[numOfTeams];
            players = new HashMap<>(numOfTeams);
            playerErrors = new HashMap<>(numOfTeams);
            recView = findViewById(R.id.teamsRecView);
            adapter = new TeamsRecyclerAdapter();
            recView.setAdapter(adapter);
            recView.setLayoutManager(new LinearLayoutManager(this));
        }

        createTeams = findViewById(R.id.createTeams);
        createTeams.setOnClickListener(v -> {
            performCreateTeams();
        });

    }

    private class TeamsViewHolder extends RecyclerView.ViewHolder{

        EditText teamNameView;
        LinearLayout parentView;
        TextView teamNumber;
        ImageView teamImage;
        View[] playerViews;

        public TeamsViewHolder(View itemView) {
            super(itemView);

            parentView = itemView.findViewById(R.id.playersParentView);
            teamNameView = itemView.findViewById(R.id.teamNameView);
            teamNumber = itemView.findViewById(R.id.teamNumber);
            teamImage = itemView.findViewById(R.id.teamImage);
        }

        public void bind(int position, Bitmap bitmap){

            teamImage.setOnClickListener(v -> {
                nowPosition = position;
                pickTeamImage();
            });

            if (bitmap != null){
                teamImage.setImageBitmap(bitmap);
            }

            String teamString = getResources().getString(R.string.team);
            teamNumber.setText(teamString.concat(String.valueOf(position + 1)));
            teamNames[position] = teamString.concat(String.valueOf(position + 1));
            teamNameView.setText(teamString.concat(String.valueOf(position + 1)));

            teamNameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    teamNames[position] = s.toString();
                    teamNameErrors[position] = false;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            String[] playerNames = new String[numOfPlayers];

            Boolean[] playerNameErrors = new Boolean[numOfPlayers];
            for (int j = 0; j < playerNameErrors.length; j++){
                playerNameErrors[j] = true;
            }

            String playerNameString =  getResources().getString(R.string.player_name);
            if (numOfPlayers > 0){
                Log.i("numOfPlayers", "TeamsViewHolder: " + numOfPlayers);
                Log.i("numOfTeams", "TeamsViewHolder: " + numOfTeams);

//                if (playerViews != null && playerViews.length > 0){
//                    parentView.removeAllViewsInLayout();
//                }

                playerViews = new View[numOfPlayers];

                for (int i = 0; i < numOfPlayers; i++){
                    EditText et =new EditText(CreateChampionTeam.this);
                    et.setBackground(getResources().getDrawable(R.drawable.edit_border));
                    et.setPadding(20, 10, 20, 10);


                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    layoutParams.setMargins(40, 10, 40, 0);
                    et.setLayoutParams(layoutParams);
                    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    et.setText(playerNameString.concat(String.valueOf(i + 1)));

                    playerNames[i] = playerNameString.concat(String.valueOf(i + 1));
                    players.put(position, playerNames);


                    et.setTextColor(getResources().getColor(R.color.black));
                    parentView.addView(et);

                    final int p_count = i;
                    et.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            playerNames[p_count] = s.toString();
                            playerNameErrors[p_count] = false;
                            players.put(position, playerNames);
                            playerErrors.put(position, playerNameErrors);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }

            ImageView iV = new ImageView(CreateChampionTeam.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    1);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            layoutParams.setMargins(20, 30, 20, 0);
            iV.setLayoutParams(layoutParams);
            iV.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            parentView.addView(iV);
        }
    }

    private class TeamsRecyclerAdapter extends RecyclerView.Adapter<TeamsViewHolder>{

        @NonNull
        @Override
        public TeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CreateChampionTeam.this).inflate(R.layout.create_team_card, parent, false);
            return new TeamsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TeamsViewHolder holder, int position) {
            holder.bind(position, tmpImg[position]);
        }

        @Override
        public int getItemCount() {
            return numOfTeams;
        }
    }

    private void pickTeamImage() {
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        GalleryConfig config = new GalleryConfig.Build()
                                .singlePhoto(false)
                                .hintOfPick("Pick image")
//                                .filterMimeTypes(new String[]{"image/jpeg"})
//                                .singlePhoto(true)
                                .limitPickPhoto(1)
                                .build();
                        GalleryActivity.openActivity(CreateChampionTeam.this, PICK_IMAGE_REQUEST_CODE, config);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {
            selectedImages = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            if (selectedImages != null && selectedImages.size() > 0){
                Bitmap bm = null;
                bm = BitmapFactory.decodeFile(selectedImages.get(0));
                tmpImg[nowPosition] = bm;
                adapter.notifyItemChanged(nowPosition);
                teamImages[nowPosition] = selectedImages.get(0);
            }
        }
    }

    public byte[] convert(String path) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        try {
            for (int readNum; (readNum = fis.read(b)) != -1; ) {
                bos.write(b, 0, readNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    private void performCreateTeams() {
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, "https://rezetopia.com/Apis/teams",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String responseString = new String(response.data);
                        Log.i("performCreateTeams", "onResponse: " + responseString);
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            if (!jsonObject.getBoolean("error")){
                                Log.i("createTeams", "onResponse: " + "successful");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("createTeamsError", "onErrorResponse: " + error.getMessage());
                if (error instanceof NetworkError) {
                    Log.i("CreateError", getResources().getString(R.string.connection_error));
                } else if (error instanceof ServerError) {
                    Log.i("CreateError", getResources().getString(R.string.server_error));
                } else if (error instanceof AuthFailureError) {
                    Log.i("CreateError", getResources().getString(R.string.connection_error));
                } else if (error instanceof ParseError) {
                    Log.i("CreateError", getResources().getString(R.string.parsing_error));
                } else if (error instanceof NoConnectionError) {
                    Log.i("CreateError", getResources().getString(R.string.connection_error));
                } else if (error instanceof TimeoutError) {
                    Log.i("CreateError", getResources().getString(R.string.time_out));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("champion_id", String.valueOf(championId));

                if (teamNames != null && teamNames.length > 0){
                    map.put("teams_size", String.valueOf(numOfTeams));
                    map.put("players_size", String.valueOf(numOfPlayers));
                    for (int i = 0; i < teamNames.length; i++){
                        if (teamNames[i] != null){
                            map.put("team".concat(String.valueOf(i)), teamNames[i]);
                        }

                        if (players.get(i) != null){
                            String[] tmpPlayerNames = players.get(i);
                            for (int j = 0; j < numOfPlayers; j++){
                                if (tmpPlayerNames[j] != null){
                                    map.put("player_".concat(String.valueOf(i)).concat("_").concat(String.valueOf(j)), tmpPlayerNames[j]);
                                }
                            }
                        }
                    }
                }
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Log.i("uploadTest", "getParams: bytes");
                Map<String, DataPart> params = new HashMap<>();

                if (teamImages != null && teamImages.length > 0){
                    for (int i = 0; i< numOfTeams; i++){
                        if (teamImages[i] != null){
                            params.put("image".concat(String.valueOf(i)), new DataPart("image".concat(String.valueOf(i)), convert(teamImages[i]), "image/jpeg"));
                        }
                    }
                }

                return params;
            }
        };

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
