package io.krito.com.rezetopia.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.AppConfig;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.helper.VolleyCustomRequest;
import io.krito.com.rezetopia.models.pojo.pages.Page;
import retrofit2.http.POST;

public class Pages extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Page> pages;
    String userId;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        recyclerView = findViewById(R.id.pagesRecView);
        progressBar = findViewById(R.id.progressPages);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        fetchPages();
    }


    private class PagesRecyclerAdapter extends RecyclerView.Adapter<PagesViewHolder>{


        @NonNull
        @Override
        public PagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Pages.this).inflate(R.layout.page_card, parent, false);
            return new PagesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PagesViewHolder holder, int position) {
            holder.bind(pages.get(position));
        }

        @Override
        public int getItemCount() {
            return pages.size();
        }
    }

    private class PagesViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;

        public PagesViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.pageImage);
            name = itemView.findViewById(R.id.pageName);
        }

        public void bind(Page page){
            if (page.getImage() != null){
                Picasso.with(Pages.this).load(page.getImage()).into(image);
            }

            if (page.getName() != null){
                name.setText(page.getName());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Pages.this, io.krito.com.rezetopia.activities.Page.class);
                    intent.putExtra("name", page.getName());
                    intent.putExtra("id", page.getId());
                    startActivity(intent);
                }
            });
        }
    }

    private void fetchPages(){
        VolleyCustomRequest request = new VolleyCustomRequest(Request.Method.GET, "https://rezetopia.com/Apis/pages?user_id=" + userId, io.krito.com.rezetopia.models.pojo.pages.Pages.class,
                new Response.Listener<io.krito.com.rezetopia.models.pojo.pages.Pages>() {
            @Override
            public void onResponse(io.krito.com.rezetopia.models.pojo.pages.Pages response) {
                Log.i("fetch_pages", "onResponse: " + response);
                if (!response.isError()){
                    if (response.getPages() != null && response.getPages().length > 0){
                        pages = new ArrayList<>(Arrays.asList(response.getPages()));
                        PagesRecyclerAdapter adapter = new PagesRecyclerAdapter();
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Pages.this));
                        progressBar.setVisibility(View.GONE);
                    }
                } else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("fetch_pages", "onErrorResponse: " + error.getMessage());
            }
        });

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
