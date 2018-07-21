package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.helper.VolleyCustomRequest;
import io.krito.com.rezetopia.models.pojo.post.ApiAlbums;

public class CreateChampion extends AppCompatActivity implements View.OnClickListener{

    EditText championName;
    MaterialSpinner typeSpinner;
    TextView playersPlus;
    TextView playersMinus;
    TextView playersNumber;
    TextView description;
    MaterialSpinner homeAwaySpinner;
    MaterialSpinner teamsSpinner;
    Button createChampion;

    int championId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_champion);

        championName = findViewById(R.id.championNameView);
        typeSpinner = findViewById(R.id.type_spinner);
        playersPlus = findViewById(R.id.txt_incr2);
        playersMinus = findViewById(R.id.txt_decr2);
        playersNumber = findViewById(R.id.txt_count2);
        homeAwaySpinner = findViewById(R.id.home_away_spinner);
        createChampion = findViewById(R.id.createChampion);
        teamsSpinner = findViewById(R.id.teams_spinner);
        description = findViewById(R.id.championDescriptionView);

        typeSpinner.setItems("League", "Knockout", "Groups");
        homeAwaySpinner.setItems("One match", "Home & Away");

        typeSpinner.setSelectedIndex(0);
        homeAwaySpinner.setSelectedIndex(0);

        /*String[] tempItems = new String[20];
        for (int i = 0; i < 20; i++){
            tempItems[i] = String.valueOf(i + 1);
        }
        teamsSpinner.setItems(Arrays.asList(tempItems));
        teamsSpinner.setSelectedIndex(0);*/

        typeSpinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (position == 0){
                String[] leagueItems = new String[20];
                for (int i = 0; i < 20; i++){
                    leagueItems[i] = String.valueOf(i + 1);
                }
                teamsSpinner.setItems(Arrays.asList(leagueItems));
            } else {
                String[] leagueItems = new String[7];
                for (int i = 0; i < 7; i++){
                    leagueItems[i] = String.valueOf((int)Math.pow(2, i + 1));
                }
                teamsSpinner.setItems(Arrays.asList(leagueItems));
            }
        });

        playersPlus.setOnClickListener(this);
        playersMinus.setOnClickListener(this);
        createChampion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_incr2:
                playersNumber.setText(String.valueOf(Integer.parseInt(playersNumber.getText().toString()) + 1));

                break;
            case R.id.txt_decr2:
                if (Integer.parseInt(playersNumber.getText().toString()) > 0){
                    playersNumber.setText(String.valueOf(Integer.parseInt(playersNumber.getText().toString()) - 1));
                }
                break;
            case R.id.createChampion:
                if (validChampion()){
                    performCreation();
//                    Log.i("numOfPlayers", "performCreation: " + playersNumber.getText().toString());
//                    Intent intent = new Intent(CreateChampion.this, CreateChampionTeam.class);
//                    intent.putExtra("players", Integer.parseInt(playersNumber.getText().toString()));
//                    intent.putExtra("teams", Integer.parseInt(String.valueOf(teamsSpinner.getItems().get(teamsSpinner.getSelectedIndex()))));
//
//                    startActivity(intent);
                }
        }
    }

    private void performCreation(){
        Log.i("CreateChampion_", "performCreation: " + "in");
        StringRequest request = new StringRequest(Request.Method.POST, "https://rezetopia.com/Apis/champions", (String response) -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.i("CreateChampion_", "performCreation: " + response);

                if (!jsonObject.getBoolean("error")){
                    championId = jsonObject.getInt("champion_id");
                    Intent intent = new Intent(CreateChampion.this, CreateChampionTeam.class);
                    intent.putExtra("players", Integer.parseInt(playersNumber.getText().toString()));
                    intent.putExtra("teams", Integer.parseInt(String.valueOf(teamsSpinner.getItems().get(teamsSpinner.getSelectedIndex()))));
                    intent.putExtra("championId", championId);
                    //startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.i("CreateChampion_", "performCreation: " + error.getMessage());
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name", championName.getText().toString());
                map.put("body", description.getText().toString());
                map.put("champion_type_id", String.valueOf(typeSpinner.getSelectedIndex() + 1));
                map.put("teams_number", String.valueOf(teamsSpinner.getItems().get(teamsSpinner.getSelectedIndex())));
                map.put("players_number", playersNumber.getText().toString());
                map.put("home_away", String.valueOf(homeAwaySpinner.getSelectedIndex()));
                return map;
            }
        };

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }

    private boolean validChampion(){
        if (!championName.getText().toString().isEmpty()){
            if (!description.getText().toString().isEmpty()){
                return true;
            } else {
                description.setError("error");
            }
        } else {
            championName.setError("error");
        }

        return false;
    }
}
