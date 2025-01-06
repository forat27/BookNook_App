package com.example.myapplicationfinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.UserProfileViewHolder> {

    private List<UserProfile> userProfileList;
    private Context con;

    public UserProfileAdapter(Context con, List<UserProfile> userProfileList) {
        this.userProfileList = userProfileList;
        this.con= con;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile_card, parent, false);
        return new UserProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        UserProfile userProfile = userProfileList.get(position);
        holder.nameTextView.setText(userProfile.getName());
        Picasso.get().load(userProfile.getProfilePicUrl()).placeholder(R.drawable.baseline_account_circle_24_green).into(holder.profileImageView);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(con, Profile.class);
            intent.putExtra("userId", userProfile.getUserId());
            con.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userProfileList.size();
    }

    public static class UserProfileViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView profileImageView;

        public UserProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.usernameTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
