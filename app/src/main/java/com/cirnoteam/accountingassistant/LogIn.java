package com.cirnoteam.accountingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


/**
 * Login Class
 * @author UZ
 * @version 1.0
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


        //TODO:检验账号密码正确性


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

