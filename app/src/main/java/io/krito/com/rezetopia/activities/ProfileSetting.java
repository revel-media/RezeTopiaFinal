package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.krito.com.rezetopia.R;

public class ProfileSetting extends AppCompatActivity implements View.OnClickListener {

    LinearLayout playerSetting;
    LinearLayout generalSetting;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        playerSetting = findViewById(R.id.playerSetting);
        generalSetting = findViewById(R.id.generalSetting);
        back = findViewById(R.id.backView);

        playerSetting.setOnClickListener(this);
        generalSetting.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.playerSetting:
                intent = new Intent(ProfileSetting.this, PlayerSetting.class);
                startActivity(intent);
                break;
            case R.id.backView:
                onBackPressed();
                break;
            case R.id.generalSetting:
                intent = new Intent(ProfileSetting.this, GeneralSetting.class);
                startActivity(intent);
                break;
        }
    }
}
