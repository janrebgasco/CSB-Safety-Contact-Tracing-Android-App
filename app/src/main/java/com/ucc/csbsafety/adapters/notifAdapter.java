package com.ucc.csbsafety.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ucc.csbsafety.NotifMoreInfo;
import com.ucc.csbsafety.R;

import java.util.List;

public class notifAdapter extends RecyclerView.Adapter<notifAdapter.ViewHolder> {
    List<String> titles;
    List<String> desc;
    List<String> dateAndTime;
    List<String> moreInfo;
    LayoutInflater inflater;
    Context context;

    public notifAdapter(Context ctx, List<String> titles, List<String> desc, List<String> dateAndTime, List<String> moreInfo,Context context){
        this.titles=titles;
        this.desc=desc;
        this.dateAndTime=dateAndTime;
        this.moreInfo = moreInfo;
        this.inflater = LayoutInflater.from(ctx);
        this.context = context;
    }

    @NonNull
    @Override
    public notifAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notif_list_item,parent,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtDesc,txtDateAnddTime;
        ConstraintLayout container;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.notifTitle);
            txtDesc = itemView.findViewById(R.id.notifDesc);
            txtDateAnddTime = itemView.findViewById(R.id.dateAndTime);
            container = itemView.findViewById(R.id.notif_container);
            imageView = itemView.findViewById(R.id.notifImg);

        }
    }
    @Override
    public void onBindViewHolder(@NonNull notifAdapter.ViewHolder holder, int position) {
        holder.txtTitle.setText(titles.get(position));
        holder.txtDesc.setText(desc.get(position));
        holder.txtDateAnddTime.setText(dateAndTime.get(position));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NotifMoreInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title",titles.get(position));
                intent.putExtra("moreInfo",moreInfo.get(position));
                intent.putExtra("dateAndTime",dateAndTime.get(position));
                context.startActivity(intent);
            }
        });
        boolean isVerifiedNotif = titles.get(position).equals("Test result verified");
        if (isVerifiedNotif){
            holder.imageView.setImageResource(R.drawable.covid_icon);
        }else{
            holder.imageView.setImageResource(R.drawable.close_contact);
        }
        //holder.Image.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
