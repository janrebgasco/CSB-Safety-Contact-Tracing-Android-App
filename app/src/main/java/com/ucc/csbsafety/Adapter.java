package com.ucc.csbsafety;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context mContext;
    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;
    ConstraintLayout containCateg;
    public static String categType="";

    interface OnItemCheckListener {
        void onItemCheck(String currentItem);
        void onItemUncheck(String currentItem);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public Adapter(Context ctx, List<String> titles, List<Integer> images, @NonNull OnItemCheckListener onItemCheckListener){
        this.mContext = ctx;
        this.titles=titles;
        this.images=images;
        this.inflater = LayoutInflater.from(ctx);
        this.onItemClick = onItemCheckListener;
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Title;
        ImageView GridIcon;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.txtImg);
            GridIcon = itemView.findViewById(R.id.prodImg);
            containCateg = itemView.findViewById(R.id.categ_container);
            checkBox = itemView.findViewById(R.id.checkBoxSymptoms);

            containCateg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(mContext, CategContainer.class);
//                    i.putExtra("categoryType",Price.getText().toString());
//                    mContext.startActivity(i);
                }
            });
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_user_check,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Title.setText(titles.get(position));
        holder.GridIcon.setImageResource(images.get(position));
        categType = titles.get(position);


        final String currentItem = titles.get(position);

        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                onItemClick.onItemCheck(currentItem);
            } else {
                onItemClick.onItemUncheck(currentItem);
            }
        });

    }


}
