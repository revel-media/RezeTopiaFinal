package io.krito.com.rezetopia.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.krito.com.rezetopia.R;

public class PageSetting extends AppCompatActivity implements View.OnClickListener {

    private static final int GENERAL_REQUEST = 1;

    int pageId;
    String pageName;

    LinearLayout privacy;
    LinearLayout general;
    LinearLayout pageLikes;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_setting);

        pageId = getIntent().getExtras().getInt("page_id", 0);

        pageLikes = findViewById(R.id.PageLikes);
        privacy = findViewById(R.id.privacySetting);
        general = findViewById(R.id.generalSetting);
        back = findViewById(R.id.back);

        pageLikes.setOnClickListener(this);
        privacy.setOnClickListener(this);
        general.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.PageLikes:
                Intent intent2 = new Intent(PageSetting.this, PageLikes.class);
                intent2.putExtra("page_id", pageId);
                startActivity(intent2);
                break;
            case R.id.privacySetting:
                Intent intent1 = new Intent(PageSetting.this, PagePrivacySetting.class);
                intent1.putExtra("page_id", pageId);
                startActivity(intent1);
                break;
            case R.id.generalSetting:
                Intent intent = new Intent(PageSetting.this, PageGeneralSetting.class);
                intent.putExtra("page_id", pageId);
                startActivityForResult(intent, GENERAL_REQUEST);
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GENERAL_REQUEST && resultCode == RESULT_OK){
            if (data != null){
                pageName = data.getExtras().getString("page_name");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (pageName != null && !pageName.isEmpty()){
            Intent intent = new Intent();
            intent.putExtra("page_name", pageName);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}
