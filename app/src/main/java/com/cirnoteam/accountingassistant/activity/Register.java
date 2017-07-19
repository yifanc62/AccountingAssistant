package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Register Class
 *
 * @author UZ
 * @version 1.1
 */
public class Register extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

    }

    public void register(View view) {
        String userName = "";
        String password_1 = "";
        String password_2 = "";
        String email = "";
        String emailMatch = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailMatch);


        EditText editText1 = (EditText) findViewById(R.id.userName);
        userName = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.password_1);
        password_1 = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.password_2);
        password_2 = editText3.getText().toString();
        EditText editText4 = (EditText) findViewById(R.id.email);
        email = editText4.getText().toString();
        Matcher matcher = pattern.matcher(email);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        AlertDialog empty = new AlertDialog.Builder(this).create();
        empty.setTitle("输入错误");
        empty.setMessage("输入框不能为空");
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        AlertDialog passwordMatchError = new AlertDialog.Builder(this).create();
        passwordMatchError.setTitle("输入错误");
        passwordMatchError.setMessage("两次密码输入不相同");
        passwordMatchError.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        AlertDialog emailError = new AlertDialog.Builder(this).create();
        DialogInterface.OnClickListener listener_3 = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        emailError.setTitle("输入错误");
        emailError.setMessage("邮箱格式错误");
        emailError.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        if (userName.equals("") || password_1.equals("") || password_2.equals("") || email.equals(""))
            empty.show();
        else if (!password_1.equals(password_2))
            passwordMatchError.show();
        else if (!matcher.matches())
            emailError.show();
        //TODO：添加新用户至数据库

        else {
            UserUtils userUtils = new UserUtils(this);
            String token = new String();
            userUtils.register(userName,password_1,token);
            userUtils.login(userName);
            Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT);
            finish();
        }
    }
}
