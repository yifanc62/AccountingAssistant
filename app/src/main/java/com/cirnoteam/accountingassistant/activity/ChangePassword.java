package com.cirnoteam.accountingassistant.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
    }

    public void changePassword (View view){
        String old_password = "";
        String new_password = "";

        EditText editText1 = (EditText) findViewById(R.id.old_password);
        old_password = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.new_password);
        new_password = editText2.getText().toString();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        AlertDialog empty = new AlertDialog.Builder(this).create();
        empty.setTitle("输入错误");
        empty.setMessage("密码不能为空");
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);

        AlertDialog repeated_password = new AlertDialog.Builder(this).create();
        repeated_password.setTitle("输入错误");
        repeated_password.setMessage("新密码不能与旧密码相同");
        repeated_password.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);

        AlertDialog old_password_wrong = new AlertDialog.Builder(this).create();
        old_password_wrong.setTitle("输入错误");
        old_password_wrong.setMessage("旧密码输入错误");
        old_password_wrong.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
        UserUtils userUtils = new UserUtils(this);

        if (old_password.equals("")||new_password.equals(""))
            empty.show();
        else if(!userUtils.getCurrentUser().getPassword().equals(old_password))
            old_password_wrong.show();
        else if (old_password.equals(new_password))
            repeated_password.show();
        else {
            if(userUtils.updateCurrentPassword(new_password))
                Toast.makeText(getApplication(),"修改成功",Toast.LENGTH_SHORT).show();
                //todo:服务器修改
        }
    }
}
