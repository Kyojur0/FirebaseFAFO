package com.example.firebasefafo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.net.URLConnection;
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
        View view = LayoutInflater.from(this.context).inflate(R.layout.items, parent, false);
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

        @SuppressLint({"IntentReset", "QueryPermissionsNeeded"})
        public ViewHolder(View itemView) {
            super(itemView);
            nameOFFile = itemView.findViewById(R.id.nameOfFile);
            itemView.setOnClickListener(view -> {
                int position = recyclerView.getChildLayoutPosition(view);
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> position " + position);
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> urls " + urls);
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> context " + context);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                Uri uri = Uri.parse(urls.get(position));
//                String mimeType = URLConnection.guessContentTypeFromName(uri.toString());
//                intent.setDataAndType(uri, mimeType);
//                context.startActivity(intent);
//                Intent intent = new Intent();
//                String googleUrl = "https://www.google.com";
//                Uri uri = Uri.parse(googleUrl);
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> url " + Uri.parse(urls.get(position)).toString());
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> url " + uri);
//                intent.resolveActivity(context.getPackageManager());
//                intent.setData(uri);
//                intent.setType(Intent.ACTION_VIEW);
//                context.startActivity(intent);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position)));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context,"No app avaialbe to handle this request", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


