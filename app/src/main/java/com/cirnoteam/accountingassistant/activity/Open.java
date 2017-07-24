package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cirnoteam.accountingassistant.R;

import static android.view.animation.AnimationUtils.loadAnimation;

/**
 * Created by Saika on 2017/7/24.
 */

public class Open extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);

        Animation animation = loadAnimation(this, R.anim.fadeup);
        ImageView chibang = (ImageView) findViewById(R.id.chibang_anim);
        TextView word = (TextView) findViewById(R.id.word_anim);
        chibang.startAnimation(animation);
        word.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
