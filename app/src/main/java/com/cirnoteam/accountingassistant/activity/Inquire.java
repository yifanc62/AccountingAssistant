package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.entity.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/7/14.
 */

public class Inquire extends AppCompatActivity {
    String date, type, account, inquire;
    float amount = 0;
    private List<String> list_date = new ArrayList<String>();
    private List<String> list_type = new ArrayList<String>();
    private List<String> list_account = new ArrayList<String>();
    private List<String> list_result = new ArrayList<String>();
    private Spinner spinner_date;
    private Spinner spinner_type;
    private Spinner spinner_account;
    private ListView listView;
    private ArrayAdapter<String> adapter_date;
    private ArrayAdapter<String> adapter_type;
    private ArrayAdapter<String> adapter_account;
    private ArrayAdapter<String> recordAdapter;
    private String[] data = {};
    private List<com.cirnoteam.accountingassistant.entity.Record> records = new ArrayList<com.cirnoteam.accountingassistant.entity.Record>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inquire);
        initActionBar();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Inquire.this, android.R.layout.simple_list_item_1, data);
        listView = (ListView) findViewById(R.id.listview_result);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RecordDetail.class);
                startActivity(intent);
            }
        });

        list_date.add("全期间");
        list_date.add("本日");
        list_date.add("本周");
        list_date.add("本月");
        list_date.add("本年");
        list_type.add("不分类");
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

        list_account.add("所有账户");
        list_account.add("账户1");
        list_account.add("账户2");


        spinner_date = (Spinner) findViewById(R.id.spinner_date);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        spinner_account = (Spinner) findViewById(R.id.spinner_account);
        adapter_date = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_date);
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
        adapter_account = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_account);
        spinner_date.setAdapter(adapter_date);
        spinner_type.setAdapter(adapter_type);
        spinner_account.setAdapter(adapter_account);

        spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                type = adapter_type.getItem(arg2).toString();
                //Toast.makeText(getApplicationContext(), adapter_type.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinner_account.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                account = adapter_type.getItem(arg2).toString();
                ///Toast.makeText(getApplicationContext(), adapter_account.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinner_date.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                date = adapter_date.getItem(arg2).toString();
                //Toast.makeText(getApplicationContext(), adapter_date.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });



    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("查询");
        toolbar.setLogo(R.drawable.ic_notifications_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black_24dp);
        //toolbar.setOnMenuItemClickListener();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void inquire(View view) {
        EditText editText = (EditText) findViewById(R.id.text_inquire);
        inquire = editText.toString();

        RecordUtils recordUtils = new RecordUtils(this);
        records = recordUtils.queryBuilder();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        long i = 1;
        for(com.cirnoteam.accountingassistant.entity.Record record:records) {
//            if(recordUtils.getRecordById(i).equals(null))
//                break;
            String newRecord = " ";
            newRecord += fm.format(record.getTime());
            newRecord += " ";
            newRecord += record.getExpense()?"收入 ":"支出 ";
            newRecord += record.getAmount();
            newRecord += " ";
            switch (record.getType()){
                case 0:newRecord += " 一日三餐";
                    break;
                case 1:newRecord += " 购物消费";
                    break;
                case 2:newRecord += " 水电煤气";
                    break;
                case 3:newRecord += " 交通花费";
                    break;
                case 4:newRecord += " 医疗消费";
                    break;
                case 5:newRecord += " 其他支出";
                    break;
                case 6:newRecord += " 经营获利";
                    break;
                case 7:newRecord += " 工资收入";
                    break;
                case 8:newRecord += " 路上捡钱";
                    break;
                case 9:newRecord += " 其他收入";
                    break;
            }


            list_result.add(newRecord);
            i++;
        }
//        list_result.add("撒打算打算");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Inquire.this, android.R.layout.simple_list_item_1, list_result);
        listView = (ListView) findViewById(R.id.listview_result);
        listView.setAdapter(adapter);
    }
}
