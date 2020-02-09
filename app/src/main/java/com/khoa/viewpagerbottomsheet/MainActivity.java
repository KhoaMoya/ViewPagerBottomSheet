package com.khoa.viewpagerbottomsheet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    View bottomSheet;
    TextView txtHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            viewPager = findViewById(R.id.viewpager);
            bottomSheet = findViewById(R.id.bottomsheet);

            txtHello = findViewById(R.id.text_hello);

            viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), bottomSheet));

            bottomSheet.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Loi",  "Main: Y: " + bottomSheet.getY());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
