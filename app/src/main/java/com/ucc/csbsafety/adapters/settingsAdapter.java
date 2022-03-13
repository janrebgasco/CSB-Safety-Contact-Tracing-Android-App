package com.ucc.csbsafety.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ucc.csbsafety.AboutUs;
import com.ucc.csbsafety.Login;
import com.ucc.csbsafety.R;
import com.ucc.csbsafety.accountSetup;
import com.ucc.csbsafety.closeContacts;
import com.ucc.csbsafety.utility.PreferenceUtils;

import java.util.List;

public class settingsAdapter extends RecyclerView.Adapter<settingsAdapter.ViewHolder> {
    private Activity activity;
    List<String> titles;
    LayoutInflater inflater;
    Context context;

    public settingsAdapter(Context ctx, List<String> titles){
        this.context = ctx;
        this.titles = titles;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public settingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.setting_list_item,parent,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView Text;
        ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Text = itemView.findViewById(R.id.settingsTitle);
            container = itemView.findViewById(R.id.settingsListCons);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull settingsAdapter.ViewHolder holder, int position) {
        holder.Text.setText(titles.get(position));
        holder.container.setOnClickListener(view -> {
            if (position == 0){
                Intent intent = new Intent(context, accountSetup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            if (position == 1){
                Intent intent = new Intent(context, closeContacts.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            if (position == 2){
                goTourl("https://www.websitepolicies.com/policies/view/7g18lt1z");
            }
            if (position == 3){
                Intent intent = new Intent(context, AboutUs.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            if (position == 4){
                goTourl("https://www.websitepolicies.com/policies/view/zteqqwfz");
            }
            if (position == 5){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Log out");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", (dialog, which) -> {
                    // Do nothing but close the dialog
                    PreferenceUtils.removeUserStatusCheckDate(context);
                    logout();
                    dialog.dismiss();
                });

                builder.setNegativeButton("NO", (dialog, which) -> {

                    // Do nothing
                    dialog.dismiss();
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        ((Activity)context).finish();
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void goTourl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        launchBrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchBrowser);
    }
}
