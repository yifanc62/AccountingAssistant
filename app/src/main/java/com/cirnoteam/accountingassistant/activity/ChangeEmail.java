package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.media.MediaCodec;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.cirnoteam.accountingassistant.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email);
        initActionBar();

        TableLayout tableLayout = (TableLayout) findViewById(R.id.cgem_anim);
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        tableLayout.startAnimation(scaleAnimation);
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_changeemail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void changeemail(View view) {
        String email = "";
        String emailmatch = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailmatch);

        EditText editText = (EditText) findViewById(R.id.new_email);
        email = editText.getText().toString();
        Matcher matcher = pattern.matcher(emailmatch);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };

        AlertDialog empty = new AlertDialog.Builder(this).create();
        empty.setTitle("输入错误");
        empty.setMessage("邮箱不能为空");
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);

        AlertDialog emailerror = new AlertDialog.Builder(this).create();
        emailerror.setTitle("输入错误");
        emailerror.setMessage("邮箱格式错误");
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);

        if (email.equals(""))
            empty.show();
        else if (!matcher.matches())
            emailerror.show();

            //TODO:修改用户邮箱
        else {

        }
    }
}
