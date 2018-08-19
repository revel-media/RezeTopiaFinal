package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.rmiri.buttonloading.ButtonLoading;

public class PageGeneralSetting extends AppCompatActivity {

    ImageView back;
    EditText pageName;
    EditText address;
    EditText phone;
    EditText website;
    EditText about;
    ButtonLoading save;
    LinearLayout all;
    ProgressBar progressBar;

    int pageId;
    String returnName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_general_setting);

        back = findViewById(R.id.back);
        pageName = findViewById(R.id.pageName);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phoneText);
        website = findViewById(R.id.websiteText);
        about = findViewById(R.id.about);
        save = findViewById(R.id.generalSave);
        all = findViewById(R.id.all_setting);
        progressBar = findViewById(R.id.progressBar);

        pageId = getIntent().getExtras().getInt("page_id", 0);
        getData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setProgress(true);
                if (validateInputs())
                    submitDate(pageName.getText().toString());
            }
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("page_name", returnName);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private boolean validateInputs(){
        if (!pageName.getText().toString().isEmpty()){
            return true;
        } else {
            pageName.setError(getResources().getString(R.string.page_name_error));
        }
        return false;
    }

    private void getData(){
        StringRequest request = new StringRequest(Request.Method.GET, "https://rezetopia.com/Apis/pages/settings?page_id=" + pageId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("get_page_general", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    progressBar.setVisibility(View.GONE);
                    all.setVisibility(View.VISIBLE);
                    if (!jsonObject.getBoolean("error")){
                        if (jsonObject.getString("name") != null && !jsonObject.getString("name").isEmpty()) {
                            pageName.setText(jsonObject.getString("name"));
                        }

                        if (jsonObject.getString("about") != null && !jsonObject.getString("about").isEmpty()){
                            about.setText(jsonObject.getString("about"));
                        }

                        if (jsonObject.getString("address") != null && !jsonObject.getString("address").isEmpty()){
                            address.setText(jsonObject.getString("address"));
                        }

                        if (jsonObject.getString("website") != null && !jsonObject.getString("website").isEmpty()){
                            website.setText(jsonObject.getString("website"));
                        }

                        if (jsonObject.getString("phone") != null && !jsonObject.getString("phone").isEmpty()){
                            phone.setText(jsonObject.getString("phone"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("get_page_general_error", "onErrorResponse: " + error.getMessage());
            }
        });

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }

    private void submitDate(String name){
        StringRequest request = new StringRequest(Request.Method.POST, "https://rezetopia.com/Apis/pages/settings", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                save.setProgress(true);
                Log.i("submit_date_response", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")){
                        returnName = name;
                        Toast.makeText(PageGeneralSetting.this, R.string.updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PageGeneralSetting.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("submit_date_error", "onErrorResponse: " + error.getMessage());
                save.setProgress(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("page_id", String.valueOf(pageId));
                map.put("name", pageName.getText().toString());
                map.put("about", about.getText().toString());
                map.put("address", address.getText().toString());
                map.put("website", about.getText().toString());
                map.put("phone", phone.getText().toString());

                return map;
            }
        };

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
