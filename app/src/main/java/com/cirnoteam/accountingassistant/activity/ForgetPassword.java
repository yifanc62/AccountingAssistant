package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.cirnoteam.accountingassistant.R;

import java.util.regex.*;

/**
 * ForgetPassword Class
 *
 * @author UZ
 * @version 1.1
 */

public class ForgetPassword extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
    }

    public void sendEmail(View view) {
        String email = "";
        String pattern_1 = ".*@.*";
        String pattern_2 = ".*..*";
        EditText editText1 = (EditText) findViewById(R.id.email);
        email = editText1.getText().toString();
        //检查格式
        AlertDialog error = new AlertDialog.Builder(this).create();
        DialogInterface.OnClickListener listener_1 = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        error.setTitle("输入错误");
        error.setMessage("邮箱格式错误或该邮箱未被绑定");
        error.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener_1);
        AlertDialog empty = new AlertDialog.Builder(this).create();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        empty.setTitle("输入错误");
        empty.setMessage("输入框不能为空");
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        if (email.equals(""))
            empty.show();
        else if (!Pattern.matches(email, pattern_1) || !Pattern.matches(email, pattern_2))
            error.show();


        //TODO: 检验邮箱是否绑定并发送邮件
    }
}
