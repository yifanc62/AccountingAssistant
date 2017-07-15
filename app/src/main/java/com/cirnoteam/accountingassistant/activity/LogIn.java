package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.cirnoteam.accountingassistant.R;


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
        String account = "";

        EditText editText1 = (EditText) findViewById(R.id.password);
        password = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.account);
        account = editText2.getText().toString();

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
        if (password.equals("") || account.equals(""))
            empty.show();
            //TODO:检验账号密码正确性

        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}

