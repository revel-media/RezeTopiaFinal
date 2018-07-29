package io.krito.com.rezetopia.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnimBarBuilder;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.json.JSONException;
import org.json.JSONObject;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AboutProfile extends AppCompatActivity {

    private static final String USER_ID_EXTRA = "user_id";

    String userId;
    ProgressBar progressBar;
    TextView bio;
    TextView hobbies;
    TextView interests;
    TextView height;
    TextView weight;
    TextView position;
    TextView country;
    TextView city;
    TextView dob;
    FrameLayout header;
    ImageView back;

    public static Intent createIntent(Context context, String id){
        Intent intent = new Intent(context, AboutProfile.class);
        intent.putExtra(id, USER_ID_EXTRA);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_profile);

        networkListener();

        userId = getIntent().getExtras().getString("id");
        Log.i(USER_ID_EXTRA, "onCreate: " + userId);



        bio = findViewById(R.id.bio);
        hobbies = findViewById(R.id.hobbies);
        interests = findViewById(R.id.interests);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        position = findViewById(R.id.position);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        dob = findViewById(R.id.dob);
        header = findViewById(R.id.aboutHeader);
        back = findViewById(R.id.backView);

        back.setOnClickListener(v -> {
            onBackPressed();
        });


        progressBar = findViewById(R.id.about_progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        fetchAbout();
    }

    private void fetchAbout(){
        StringRequest post = new StringRequest(Request.Method.GET, "https://rezetopia.com/Apis/about?user_id=" + userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    Log.i("about_result", "onResponse: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")){
                        if (jsonObject.getString("about") != null){
                            bio.setText(jsonObject.getString("about"));
                        } else {
                            bio.setText("none");
                        }

                        if (jsonObject.getString("hobbies") != null){
                            hobbies.setText(jsonObject.getString("hobbies"));
                        } else {
                            hobbies.setText("none");
                        }

                        if (jsonObject.getString("height") != null){
                            height.setText(height.getText() + ": " + jsonObject.getString("height"));
                        }

                        if (jsonObject.getString("interests") != null){
                            interests.setText(jsonObject.getString("interests"));
                        } else {
                            interests.setText("none");
                        }

                        if (jsonObject.getString("weight") != null){
                            weight.setText(weight.getText() + ": " + jsonObject.getString("weight"));
                        }

                        if (jsonObject.getString("position") != null){
                            position.setText(position.getText() + ": " + jsonObject.getString("position"));
                        }

                        if (jsonObject.getString("country") != null){
                            country.setText(country.getText() + ": " + jsonObject.getString("country"));
                        }

                        if (jsonObject.getString("city") != null){
                            city.setText(city.getText() + ": " + jsonObject.getString("city"));
                        }

                        if (jsonObject.getString("birthday") != null){
                                dob.setText(dob.getText() + ": " + jsonObject.getString("birthday"));
                        }
                    } else {
                        Log.i("about_result", "onResponse: " + "error -> true");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                String message = null;
                Log.i("about_result", "onErrorResponse: " + error.getMessage());
                if (error instanceof NetworkError) {
                    //message = String.valueOf();
                    //infoCallback.onError(R.string.checkingNetwork);
                    return;
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                    //infoCallback.onError(R.string.server_error);
                    return;
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    //infoCallback.onError(R.string.connection_error);
                    return;
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                    //infoCallback.onError(R.string.parsing_error);
                    return;
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    //infoCallback.onError(R.string.connection_error);
                    return;
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                    //infoCallback.onError(R.string.time_out);
                    return;
                }
                //infoCallback.onError(R.string.time_out);
            }
        });

        /*
        * {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("method", "get_skills");
                params.put("id", userId);
                return params;
            }
        }*/
        RezetopiaApp.getInstance().getRequestQueue().add(post);
    }

    private void networkListener(){
        ReactiveNetwork.observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.getState() == NetworkInfo.State.CONNECTED){
                        Log.i("internetC", "onNext: " + "Connected");
                    } else if (connectivity.getState() == NetworkInfo.State.SUSPENDED){
                        Log.i("internetC", "onNext: " + "LowNetwork");
                    } else {
                        Log.i("internetC", "onNext: " + "NoInternet");
                        Flashbar.Builder builder = new Flashbar.Builder(this);
                        builder.gravity(Flashbar.Gravity.BOTTOM)
                                .backgroundColor(R.color.red2)
                                .enableSwipeToDismiss()
                                .message(R.string.checkingNetwork)
                                .enterAnimation(new FlashAnimBarBuilder(AboutProfile.this).slideFromRight().duration(200))
                                .build().show();
                    }
                });
    }

}
