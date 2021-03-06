package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.BookUtils;
import com.cirnoteam.accountingassistant.database.UserUtils;
import com.cirnoteam.accountingassistant.entity.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;


/**
 * Login Class
 *
 * @author UZ
 * @version 1.1
 */

public class LogIn extends AppCompatActivity {

    AlertDialog empty;
    AlertDialog mismatch;
    AlertDialog notFoundUser;
    private String device;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);


        UserUtils userUtils = new UserUtils(this);
        if (userUtils.getCurrentUser() == null)
            Toast.makeText(getApplicationContext(), "当前无用户登录", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
        }
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        TableLayout ta = (TableLayout) findViewById(R.id.T4);
        ta.startAnimation(scaleAnimation);
    }

    public void toRegister(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void toForgetPassword(View view) {
        Intent intent = new Intent(this, TransitPassword.class);
        startActivity(intent);
    }

    public void toMainActivity(View view) {

        AlertDialog load = new AlertDialog.Builder(this).create();
        load.setMessage("数据处理中，请勿进行其他操作");
        load.show();

        EditText editText1 = (EditText) findViewById(R.id.password);
        final String password = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.userName);
        final String userName = editText2.getText().toString();

        empty = new AlertDialog.Builder(this).create();
        notFoundUser = new AlertDialog.Builder(this).create();
        mismatch = new AlertDialog.Builder(this).create();

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
        empty.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
        mismatch.setTitle("密码错误");
        mismatch.setMessage("密码与用户名不匹配");
        mismatch.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
        notFoundUser.setTitle("用户名错误");
        notFoundUser.setMessage("未找到该用户");
        notFoundUser.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);

        if (password.equals("") || userName.equals(""))
            empty.show();
        else {
            new Thread() {
                public void run() {
                    LoginByPost(userName, EncoderByMd5(password));
                }
            }.start();

        }
    }

    public void LoginByPost(String userName, String userPass) {

        try {
            UserUtils userUtils = new UserUtils(this);
            if (userUtils.getUser(userName) == null) {
                device = userUtils.getDefaultDeviceName();
                uuid = userUtils.generateUuid();
            } else {
                device = userUtils.getUser(userName).getDevicename();
                uuid = userUtils.getUser(userName).getUuid();
            }

            // 请求的地址
            String spec = "http://cirnoteam.varkarix.com/login";
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
                    + "&device=" + URLEncoder.encode(device, "UTF-8")
                    + "&uuid=" + URLEncoder.encode(uuid, "UTF-8");
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
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

                final String message = jsonObject.getString("message");

                if (code == 200) {
                    try {
                        String token = jsonObject.getJSONObject("entity").getString("token");
                        if (userUtils.getUser(userName) == null) {
                            userUtils.register(userName, userPass, token, uuid, device);
                        }
                        if (userUtils.getUser(userName).getPassword().equals(userPass)) {
                            userUtils.login(userName);
                            userUtils.refreshCurrentToken(token);
                            BookUtils bookUtils = new BookUtils(this);
                            AccountUtils accountUtils = new AccountUtils(this);
                            if (bookUtils.getAllBooks(userUtils.getCurrentUsername()).size() == 0) {
                                long id = bookUtils.addBook(userName, "默认账本");
                                accountUtils.addAccount(id, 0, 1000F, "默认账户");
                            }
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 在这里把返回的数据写在控件上 会出现什么情况尼
                                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        } else
                            mismatch.show();

                    } catch (Exception e) {
                        notFoundUser.show();
                    }
                } else {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里把返回的数据写在控件上 会出现什么情况尼
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在这里把返回的数据写在控件上 会出现什么情况尼
                        Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String EncoderByMd5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return convertByteToHexString(md5.digest(str.getBytes()));
        } catch (Exception e) {
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


