package com.ucc.csbsafety;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.ucc.csbsafety.adapters.fragPagerAdapter;

public class NewUserSlider extends AppCompatActivity {
    ViewPager2 imageContainer;
    ImageView[] dots;
    LinearLayout layout;
    fragPagerAdapter adapter;
    Button btnNext;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_slider);
        adapter = new fragPagerAdapter(this,position);
        btnNext = findViewById(R.id.btnNext);

        imageContainer = findViewById(R.id.viewpager);
        layout = findViewById(R.id.dotsContainer);

        dots = new ImageView[3];

        imageContainer.setAdapter(adapter);

        setIndicators();

        imageContainer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedDots(position);
                super.onPageSelected(position);
            }
        });
        btnNext.setOnClickListener(view -> {
            if (position <= 1){
                position++;
                imageContainer.setCurrentItem(position);
            }
            else{
                Intent i = new Intent(NewUserSlider.this,getStarted.class);
                startActivity(i);
            }
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void selectedDots(int position) {
        this.position = position;
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.tab_indicator));
            } else {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.tab_indicator_light));
            }
        }
    }

    private void setIndicators() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 0, 5, 0);
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            //dots[i].setMaxHeight(12);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.tab_indicator));
            dots[i].setLayoutParams(lp);
            layout.addView(dots[i]);
        }

    }

    public void gotoGetStarted(View view) {
        finish();
        Intent i = new Intent(this,getStarted.class);
        startActivity(i);
    }
}