package io.krito.com.rezetopia.activities;

import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;

public class PlayerSetting extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> positions = new ArrayList<>();
    Map<String, Boolean> positionChecked = new HashMap<>();

    ImageView backView;
    EditText weightView;
    EditText heightView;
    Button saveChanges;
    FrameLayout lwf;
    FrameLayout rwf;
    FrameLayout cf;
    FrameLayout ss;
    FrameLayout am;
    FrameLayout lm;
    FrameLayout rm;
    FrameLayout cm;
    FrameLayout dm;
    FrameLayout cb;
    FrameLayout rb;
    FrameLayout lb;
    FrameLayout sw;
    FrameLayout gk;
    TextView lwf_t;
    TextView rwf_t;
    TextView cf_t;
    TextView ss_t;
    TextView am_t;
    TextView lm_t;
    TextView rm_t;
    TextView cm_t;
    TextView dm_t;
    TextView cb_t;
    TextView lb_t;
    TextView rb_t;
    TextView sw_t;
    TextView gk_t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_setting);

        init();
    }

    private void init(){
        backView = findViewById(R.id.back);
        weightView = findViewById(R.id.weight);
        heightView = findViewById(R.id.height);
        saveChanges = findViewById(R.id.saveChanges);
        lwf = findViewById(R.id.lwf);
        rwf = findViewById(R.id.rwf);
        cf = findViewById(R.id.cf);
        ss = findViewById(R.id.ss);
        am = findViewById(R.id.am);
        rm = findViewById(R.id.rm);
        lm = findViewById(R.id.lm);
        cm = findViewById(R.id.cm);
        dm = findViewById(R.id.dm);
        lb = findViewById(R.id.lb);
        rb = findViewById(R.id.rb);
        sw = findViewById(R.id.sw);
        cb = findViewById(R.id.cb);
        gk = findViewById(R.id.gk);
        lwf_t = findViewById(R.id.lwf_t);
        rwf_t = findViewById(R.id.rwf_t);
        cf_t = findViewById(R.id.cf_t);
        ss_t = findViewById(R.id.ss_t);
        am_t = findViewById(R.id.am_t);
        lm_t = findViewById(R.id.lm_t);
        rm_t = findViewById(R.id.rm_t);
        cm_t = findViewById(R.id.cm_t);
        dm_t = findViewById(R.id.dm_t);
        lb_t = findViewById(R.id.lb_t);
        rb_t = findViewById(R.id.rb_t);
        cb_t = findViewById(R.id.cb_t);
        sw_t = findViewById(R.id.sw_t);
        gk_t = findViewById(R.id.gk_t);

        lwf.setOnClickListener(this);
        rwf.setOnClickListener(this);
        cf.setOnClickListener(this);
        ss.setOnClickListener(this);
        am.setOnClickListener(this);
        rm.setOnClickListener(this);
        lm.setOnClickListener(this);
        cm.setOnClickListener(this);
        dm.setOnClickListener(this);
        lb.setOnClickListener(this);
        rb.setOnClickListener(this);
        cb.setOnClickListener(this);
        sw.setOnClickListener(this);
        gk.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
        backView.setOnClickListener(this);

        positionChecked.put("lwf", false);
        positionChecked.put("rwf", false);
        positionChecked.put("cf", false);
        positionChecked.put("ss", false);
        positionChecked.put("am", false);
        positionChecked.put("rm", false);
        positionChecked.put("lm", false);
        positionChecked.put("cm", false);
        positionChecked.put("dm", false);
        positionChecked.put("lb", false);
        positionChecked.put("rb", false);
        positionChecked.put("cb", false);
        positionChecked.put("sw", false);
        positionChecked.put("gk", false);
    }

    private void changeBackground(FrameLayout frame, String txt){
        TransitionDrawable transition = (TransitionDrawable) frame.getBackground();

        if (positionChecked.get(txt)){
            transition.reverseTransition(500);
            positionChecked.put(txt, false);
        } else {
            transition.startTransition(500);
            positionChecked.put(txt, true);
            if (positions.size() > 0 && !positions.contains(txt)){
                positions.add(txt);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lwf:
                changeBackground(lwf, "lwf");
                break;
            case R.id.rwf:
                changeBackground(rwf, "rwf");
                break;
            case R.id.cf:
                changeBackground(cf, "cf");
                break;
            case R.id.ss:
                changeBackground(ss, "ss");
                break;
            case R.id.cm:
                changeBackground(cm, "cm");
                break;
            case R.id.dm:
                changeBackground(dm, "dm");
                break;
            case R.id.am:
                changeBackground(am, "am");
                break;
            case R.id.lm:
                changeBackground(lm, "lm");
                break;
            case R.id.rm:
                changeBackground(rm, "rm");
                break;
            case R.id.cb:
                changeBackground(cb, "cb");
                break;
            case R.id.rb:
                changeBackground(rb, "rb");
                break;
            case R.id.lb:
                changeBackground(lb, "lb");
                break;
            case R.id.sw:
                changeBackground(sw, "sw");
                break;
            case R.id.gk:
                changeBackground(gk, "gk");
                break;
            case R.id.saveChanges:
                if (validSetting()){
                    performEditSetting();
                }
                break;
            case R.id.back:
                onBackPressed();
        }
    }

    private boolean validSetting(){
        if (weightView.getText().toString().isEmpty() && heightView.getText().toString().isEmpty() && positions.size() > 0){
            return true;
        }
        return false;
    }

    private void performEditSetting(){
        String url = "";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {

                } else if (error instanceof ServerError) {

                } else if (error instanceof AuthFailureError) {

                } else if (error instanceof ParseError) {

                } else if (error instanceof NoConnectionError) {

                } else if (error instanceof TimeoutError) {

                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("weight", weightView.getText().toString());
                map.put("height", heightView.getText().toString());

                if (positions.size() > 0){
                    map.put("position_size", String.valueOf(positions.size()));
                    for (int i = 0; i < positions.size(); i++) {
                        map.put("position" + i, positions.get(i));
                    }
                }

                return map;
            }
        };


        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
