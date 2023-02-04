package com.example.firebasefafo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final RecyclerView recyclerView;
    private final Context context;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void update(String name, String url) {
        items.add(name);
        urls.add(url);
        notifyDataSetChanged(); // refreshes the recycler view automatically
    }

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // intialize the elemnts of indiv, items...
        holder.nameOFFile.setText(items.get(position));
    }

    @Override
    public int getItemCount() { // get the number of items
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameOFFile;

        public ViewHolder(View itemView) {
            super(itemView);
            nameOFFile = itemView.findViewById(R.id.nameOfFile);
            itemView.setOnClickListener(view -> {
                int position = recyclerView.getChildLayoutPosition(view);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> position " + position);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> urls " + urls);
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse(urls.get(position)), Intent.ACTION_VIEW);
                context.startActivity(intent);
            });
        }
    }
}
