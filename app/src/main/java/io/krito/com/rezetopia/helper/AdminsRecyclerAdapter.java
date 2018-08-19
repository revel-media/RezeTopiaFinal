package io.krito.com.rezetopia.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.krito.com.rezetopia.R;
import io.krito.com.rezetopia.models.pojo.pages.Admin;

/**
 * Created by Ahmed Ali on 8/12/2018.
 */

class AdminsRecyclerAdapter extends RecyclerView.Adapter<AdminsRecyclerAdapter.AdminsViewHolder>{

    private Context context;
    private ArrayList<Admin> pageAdmins;

    public AdminsRecyclerAdapter(Context context, ArrayList<Admin> pageAdmins) {
        this.context = context;
        this.pageAdmins = pageAdmins;
    }

    @NonNull
    @Override
    public AdminsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_horizontal, parent,false);
        return new AdminsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminsViewHolder holder, int position) {
        holder.bind(pageAdmins.get(position));
    }

    @Override
    public int getItemCount() {
        return pageAdmins.size();
    }

    class AdminsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView adminPP;
        TextView adminName;

        public AdminsViewHolder(View itemView) {
            super(itemView);

            adminPP = itemView.findViewById(R.id.userPP);
            adminName = itemView.findViewById(R.id.username);
        }

        public void bind(Admin admin){
            if (admin.getPp() != null){
                Picasso.with(context).load(admin.getPp()).into(adminPP);
            }

            if (admin.getUsername() != null){
                adminName.setText(admin.getUsername());
            }
        }
    }
}

