package com.cirnoteam.accountingassistant.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ChangePassword extends AppCompatActivity {
    private int code;
    private String message;
    private String resetToken;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        Intent intent = getIntent();
        resetToken = intent.getStringExtra("resetToken");
        userName = intent.getStringExtra("userName");
        Toast.makeText(getApplicationContext(), "已向您的邮箱发送验证邮件", Toast.LENGTH_SHORT).show();

        TableLayout tableLayout = (TableLayout) findViewById(R.id.table1);
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        tableLayout.startAnimation(scaleAnimation);
    }

    public void changePassword(View view) {

        EditText editText2 = (EditText) findViewById(R.id.new_password);
        final String new_password = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.code);
        final String VCode = editText3.getText().toString();


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
        final UserUtils userUtils = new UserUtils(this);

        if (VCode.equals("") || new_password.equals(""))
            empty.show();
        else if (userUtils.getCurrentPassword().equals(new_password))
            repeated_password.show();
        else {
            new Thread() {
                public void run() {
                    changePasswordByPost(resetToken, userName, VCode, new_password);
                }
            }.start();
        }
    }

    public void changePasswordByPost(String resetToken, String userName, String VCode, String newPassword) {
        try {


            String spec = "http://cirnoteam.varkarix.com/reset";
            URL url = new URL(spec);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "username=" + URLEncoder.encode(userName, "UTF-8")
                    + "&resetToken=" + URLEncoder.encode(resetToken, "UTF-8")
                    + "&password" + URLEncoder.encode(newPassword, "UTF-8")
                    + "&code" + URLEncoder.encode(VCode, "UTF-8");
            urlConnection.setRequestProperty("Connection", "keep-alive");

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();

                final String result = new String(baos.toByteArray());
                JSONObject jsonObject = new JSONObject(result);

                code = jsonObject.getInt("code");
                message = jsonObject.getString("message");

                if (code == 200) {
                    UserUtils userUtils = new UserUtils(this);
                    userUtils.updateCurrentPassword(newPassword);
                    finish();

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "修改失败：" + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
