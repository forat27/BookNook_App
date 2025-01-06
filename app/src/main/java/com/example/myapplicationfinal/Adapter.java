package com.example.myapplicationfinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class Adapter extends RecyclerView.Adapter<Adapter.Recommednedholder> {


    private ArrayList<book_card> Recommendedbooks;
    Context con;


    public Adapter(Context con, ArrayList<book_card> Recommendedbooks) {
        this.Recommendedbooks = Recommendedbooks;
        this.con = con;
    }



    @Override
    public Recommednedholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_one_card, parent, false);
        return new Recommednedholder(view);
    }

    @Override
    public void onBindViewHolder(Recommednedholder holder, int position) {
        book_card book = Recommendedbooks.get(position);
        holder.title.setText(book.getTitle());
        holder.storename.setText(book.getName());
        holder.price.setText(String.valueOf(book.getPrice()));
        holder.location.setText(String.valueOf(book.getLocation()));
        Picasso.get().load(book.getImg()).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(con, Profile.class);
            intent.putExtra("userId", book.getUserId());
            con.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Recommendedbooks.size();
    }

    public class Recommednedholder extends RecyclerView.ViewHolder {
        TextView title, storename, price, location;
        ImageView img;

        public Recommednedholder(View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.text1);
            storename = itemView.findViewById(R.id.text3);
            price = itemView.findViewById(R.id.text2);
            location = itemView.findViewById(R.id.text4);

        }
    }
}