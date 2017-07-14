package com.cirnoteam.accountingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cirnoteam.com.cirnoteam.database.SaveDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cirnoteam.accountingassistant.R.id.amount_edit;
import static com.cirnoteam.accountingassistant.R.id.remark_edit;
import static com.cirnoteam.accountingassistant.R.id.time_edit;

/**
 * Created by Saika on 2017/7/13.
 */

public class NewRecord extends AppCompatActivity{
    //将要直接插入数据库的临时变量
    String expense,remark,amount,time;
    String type, account;

    private List<String> list_inout = new ArrayList<String>();
    private List<String> list_type = new ArrayList<String>();
    private List<String> list_account = new ArrayList<String>();
    private Spinner spinner_inout;
    private Spinner spinner_type;
    private Spinner spinner_account;
    private ArrayAdapter<String> adapter_inout;
    private ArrayAdapter<String> adapter_type;
    private ArrayAdapter<String> adapter_account;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_record);

        list_inout.add("支出");
        list_inout.add("收入");
        list_type.add("一日三餐");
        list_type.add("购物消费");
        list_type.add("水电煤气");
        list_type.add("交通花费");
        list_type.add("医疗消费");
        list_type.add("其他支出");
        list_type.add("经营获利");
        list_type.add("工资收入");
        list_type.add("路上捡钱");
        list_type.add("其他收入");
        while (true) {
            //TODO 查询数据库中的账户表，添加到list_account
            break;
        }


        spinner_inout = (Spinner) findViewById(R.id.spinner_inout);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        spinner_account = (Spinner) findViewById(R.id.spinner_account);
        adapter_inout = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_inout);
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
        adapter_account = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_account);
        spinner_inout.setAdapter(adapter_inout);
        spinner_type.setAdapter(adapter_type);
        spinner_account.setAdapter(adapter_account);

        //设置默认值：时间
        SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        EditText editText_time = (EditText) findViewById(time_edit);
        editText_time.setText(str);

        /*******************监听事件***********************/
        spinner_inout.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                expense = String.valueOf(arg2);
                //Toast.makeText(getApplicationContext(), adapter_inout.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
        spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                type = String.valueOf(arg2);
                //Toast.makeText(getApplicationContext(), adapter_type.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
        spinner_account.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                account = adapter_account.getItem(arg2).toString();
                //Toast.makeText(getApplicationContext(), adapter_account.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
    }
    public void toCreate(View view) {
        EditText editText_amount = (EditText) findViewById(amount_edit);
        if(!TextUtils.isEmpty(editText_amount.getText()))
            amount = editText_amount.getText().toString();
        else
            amount = "0";
        time = ((EditText) findViewById(time_edit)).getText().toString();
        remark = ((EditText) findViewById(remark_edit)).getText().toString();
        //Toast.makeText(getApplicationContext(), amount+inout+account+type+time_edit+remark_edit, Toast.LENGTH_SHORT).show();
        if(SaveDB.saveRecord(this.getFilesDir().toString(),expense,amount,remark,type,time))
            Toast.makeText(getApplicationContext(), "存储成功", Toast.LENGTH_SHORT).show();

        finish();
    }
    public void back(View view){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }
}
