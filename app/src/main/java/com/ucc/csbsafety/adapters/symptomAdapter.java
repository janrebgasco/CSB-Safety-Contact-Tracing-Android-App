package com.ucc.csbsafety.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ucc.csbsafety.R;

import java.util.List;

public class symptomAdapter extends RecyclerView.Adapter<symptomAdapter.ViewHolder> {
    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;

    public symptomAdapter(Context ctx, List<String> titles, List<Integer> images){
        this.titles=titles;
        this.images=images;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public symptomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.symptoms_list_item,parent,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView Text;
        ImageView Image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.img);
            Text = itemView.findViewById(R.id.txt);

        }
    }
    @Override
    public void onBindViewHolder(@NonNull symptomAdapter.ViewHolder holder, int position) {
        holder.Text.setText(titles.get(position));
        holder.Image.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
