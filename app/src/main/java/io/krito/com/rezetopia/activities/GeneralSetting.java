package io.krito.com.rezetopia.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import io.krito.com.rezetopia.R;

public class GeneralSetting extends AppCompatActivity implements View.OnClickListener{

    ImageView back;
    EditText username;
    EditText email;
    EditText country;
    EditText city;
    EditText about;
    Button generalSave;
    EditText currentPassword;
    EditText newPassword;
    Button passwordSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_setting);

        back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        email = findViewById(R.id.email);
        about = findViewById(R.id.about);
        generalSave = findViewById(R.id.generalSave);
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        passwordSave = findViewById(R.id.passwordSave);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                onBackPressed();
                break;
        }
    }
}
