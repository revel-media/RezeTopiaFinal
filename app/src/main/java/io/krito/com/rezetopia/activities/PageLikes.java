package io.krito.com.rezetopia.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.application.RezetopiaApp;
import io.krito.com.rezetopia.helper.VolleyCustomRequest;

public class PageLikes extends AppCompatActivity {

    int pageId;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_likes);

        recyclerView = findViewById(R.id.pageLikesRecView);

        pageId = getIntent().getExtras().getInt("page_id", 0);
    }

    private class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesViewHolder>{

        @NonNull
        @Override
        public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PageLikes.this).inflate(R.layout.view_friends_card, parent, false);
            return new LikesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class LikesViewHolder extends RecyclerView.ViewHolder {

        ImageView pp;
        TextView username;
        Button addF;

        LikesViewHolder(View itemView) {
            super(itemView);

            pp = itemView.findViewById(R.id.suggestedImageView);
            username = itemView.findViewById(R.id.suggestedUsernameView);
            addF = itemView.findViewById(R.id.suggestedAddButton);
        }

        public void bind(){

        }
    }

    private void getData(){
        VolleyCustomRequest request = new VolleyCustomRequest(Request.Method.GET, "", null, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RezetopiaApp.getInstance().getRequestQueue().add(request);
    }
}
