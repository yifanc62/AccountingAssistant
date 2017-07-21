package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;


/**
 * Login Class
 *
 * @author UZ
 * @version 1.1
 */

public class LogIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

//        try{
//            UserUtils userUtils = new UserUtils(this);
//            userUtils.getCurrentUser();
//            finish();
//            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(intent);
//        }
//        catch (Exception e){
//            Toast.makeText(getApplicationContext(),"当前无用户登录",Toast.LENGTH_SHORT).show();
//        }

    }

    public void toRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void toForgetPassword(View view) {
        Intent intent = new Intent(this, ForgetPassword.class);
        startActivity(intent);
    }

    public void toMainActivity(View view) {
        String password = "";
        String userName = "";

        EditText editText1 = (EditText) findViewById(R.id.password);
        password = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.userName);
        userName = editText2.getText().toString();

        AlertDialog empty = new AlertDialog.Builder(this).create();
        AlertDialog notFoundUser = new AlertDialog.Builder(this).create();
        AlertDialog mismatch = new AlertDialog.Builder(this).create();

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
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定",listener);
        mismatch.setTitle("密码错误");
        mismatch.setMessage("密码与用户名不匹配");
        mismatch.setButton(DialogInterface.BUTTON_POSITIVE, "确定",listener);
        notFoundUser.setTitle("用户名错误");
        notFoundUser.setMessage("未找到该用户");
        notFoundUser.setButton(DialogInterface.BUTTON_POSITIVE, "确定",listener);

        if (password.equals("") || userName.equals(""))
            empty.show();
        else {
            //TODO:检验账号密码正确性
            UserUtils userUtils = new UserUtils(this);
            try{
                if(userUtils.getUser(userName).getPassword().equals(password)){
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                else
                    mismatch.show();
            }
            catch (Exception e){
                notFoundUser.show();
            }
        }
    }
}

