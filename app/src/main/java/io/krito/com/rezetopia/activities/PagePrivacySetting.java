package io.krito.com.rezetopia.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.rmiri.buttonloading.ButtonLoading;

public class PagePrivacySetting extends AppCompatActivity {

    MaterialSpinner spinner;
    ProgressBar progressBar;
    LinearLayout all;
    ButtonLoading save;

    String role;
    int pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_privacy_setting);

        spinner = findViewById(R.id.teams_spinner);
        progressBar = findViewById(R.id.progressBar);
        all = findViewById(R.id.all_setting);
        save = findViewById(R.id.generalSave);

        pageId = getIntent().getExtras().getInt("page_id", 0);

        spinner.setItems("everyone", "only_admins");
        spinner.setSelectedIndex(1);
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            role = item.toString();
        });

        //todo new

        save.setOnClickListener(v -> {
            save.setProgress(true);
            submitData();
        });

        getDate();
    }

    private void getDate(){
        StringRequest request = new StringRequest(Request.Method.GET, "https://www.rezetopia.com/Apis/pages/settings/privacy?page_id=" + pageId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("get_date_privacy", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")){
                        progressBar.setVisibility(View.GONE);
                        all.setVisibility(View.VISIBLE);

                        if (jsonObject.getString("privacy") != null && !jsonObject.getString("privacy").isEmpty()){
                            if (jsonObject.getString("privacy").contentEquals("everyone")){
                                spinner.setSelectedIndex(0);
                            } else {
                                spinner.setSelectedIndex(1);
                            }
                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("get_date_privacy_error", "onErrorResponse: " + error.getMessage());
            }
        });

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }

    private void submitData(){
        StringRequest request = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("submit_data_privacy", "onResponse: " + response);
                save.setProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("submit_data_privacy_error", "onResponse: " + error.getMessage());
                save.setProgress(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("page_id", String.valueOf(pageId));
                map.put("role", role);

                return map;
            }
        };

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
