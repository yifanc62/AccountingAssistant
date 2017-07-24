package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.UserUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        Toast.makeText(getApplicationContext(),"数据处理中，请不要进行其他操作",Toast.LENGTH_SHORT).show();

        String emailMatch = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailMatch);


        EditText editText1 = (EditText) findViewById(R.id.userName);
        final String userName = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.password_1);
        final String password_1 = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.password_2);
        final String password_2 = editText3.getText().toString();
        EditText editText4 = (EditText) findViewById(R.id.email);
        final String email = editText4.getText().toString();
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

        emailError.setTitle("输入错误");
        emailError.setMessage("邮箱格式输入错误");
        emailError.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);

        if (userName.equals("") || password_1.equals("") || password_2.equals("") || email.equals(""))
            empty.show();
        else if (!password_1.equals(password_2))
            passwordMatchError.show();
        else if (!matcher.matches())
            emailError.show();
        else {
            new Thread() {
                public void run() {
                    registerByPost(userName,EncoderByMd5(password_1),email);
                }
            }.start();
        }
    }

    public void registerByPost(String userName, String userPass,String email) {

        try {

            // 请求的地址
            String spec = "http://cirnoteam.varkarix.com/register";
            // 根据地址创建URL对象
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data = "username=" + URLEncoder.encode(userName, "UTF-8")
                    + "&password=" + URLEncoder.encode(userPass, "UTF-8")
                    + "&email=" + URLEncoder.encode(email, "UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            int c = urlConnection.getResponseCode();
            if (c == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
                JSONObject jsonObject = new JSONObject(result);
                int code = jsonObject.getInt("code");
                String token = jsonObject.getJSONObject("entity").getString("activateToken");
                final String message = jsonObject.getString("message");

                if(code == 200){
                    Intent intent = new Intent(this,Activate.class);
                    intent.putExtra("token",token);
                    intent.putExtra("userName",userName);
                    intent.putExtra("password",userPass);
                    startActivity(intent);
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"注册成功，已向您邮箱发送验证邮件",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String EncoderByMd5(String str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return convertByteToHexString(md5.digest(str.getBytes()));
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }


    public static String convertByteToHexString(byte[] bytes) {
        String result = "";
        for (byte aByte : bytes) {
            int temp = aByte & 0xff;
            String tempHex = Integer.toHexString(temp);
            if (tempHex.length() < 2)
                result += "0" + tempHex;
            else
                result += tempHex;
        }
        return result;
    }
}
