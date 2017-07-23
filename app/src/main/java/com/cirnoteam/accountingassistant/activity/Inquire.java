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
import android.widget.Toast;


import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.entity.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pc on 2017/7/14.
 */

public class Inquire extends AppCompatActivity {
    String inquire;
    Date startDate, endDate;
    Long accountId;
    int type, expenseFlag;
    float amount = 0;
    boolean expense;
    private List<String> list_date = new ArrayList<String>();
    private List<String> list_type = new ArrayList<String>();
    private List<String> list_account = new ArrayList<String>();
    private List<String> list_expense = new ArrayList<String>();
    private Spinner spinner_date;
    private Spinner spinner_type;
    private Spinner spinner_account;
    private Spinner spinner_expense;
    private ListView listView;
    private ArrayAdapter<String> adapter_date;
    private ArrayAdapter<String> adapter_type;
    private ArrayAdapter<String> adapter_account;
    private ArrayAdapter<String> adapter_expense;
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
        AccountUtils u = new AccountUtils(this);
        final List<Account> list_accounts = u.getAccountsOfBook(1L);
        for(int i=0;i<list_accounts.size();i++) {
            list_account.add(u.getDefaultAccountName(list_accounts.get(i).getType()) + " " + list_accounts.get(i).getName());
            //获得账户类型名称和账户名字，填入spinner
        }
        list_expense.add("全部");
        list_expense.add("收入");
        list_expense.add("支出");

        spinner_date = (Spinner) findViewById(R.id.spinner_date);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        spinner_account = (Spinner) findViewById(R.id.spinner_account);
        spinner_expense = (Spinner) findViewById(R.id.spinner_expense);
        adapter_date = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_date);
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_type);
        adapter_account = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_account);
        adapter_expense = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_expense);
        spinner_date.setAdapter(adapter_date);
        spinner_type.setAdapter(adapter_type);
        spinner_account.setAdapter(adapter_account);
        spinner_expense.setAdapter(adapter_expense);

        spinner_type.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                type = arg2==0 ? -1 : arg2-1;
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
                accountId = arg2==0 ? -1L : list_accounts.get(arg2-1).getId();
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
                Calendar cal = Calendar.getInstance();
                try {
                    switch (arg2) {
                        case 0://全周期
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            startDate = df.parse("2015-01-01 00:00:00");
                            endDate = Calendar.getInstance().getTime();
                            break;
                        case 1://本日
                            cal = Calendar.getInstance();
                            endDate = cal.getTime();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            startDate = cal.getTime();
                            break;
                        case 2://本周
                            cal = Calendar.getInstance();
                            endDate = cal.getTime();
                            cal.set(Calendar.DAY_OF_WEEK, -5);//存疑，不知道为什么设为零会跳到周六
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            startDate = cal.getTime();
                            break;
                        case 3://本月
                            cal = Calendar.getInstance();
                            endDate = cal.getTime();
                            cal.set(Calendar.WEEK_OF_MONTH, 0);
                            cal.set(Calendar.DAY_OF_WEEK, 0);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            startDate = cal.getTime();
                            break;
                        case 4://本年
                            cal = Calendar.getInstance();
                            endDate = cal.getTime();
                            cal.set(Calendar.MONTH, 0);
                            cal.set(Calendar.WEEK_OF_MONTH, 0);
                            cal.set(Calendar.DAY_OF_WEEK, 0);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            startDate = cal.getTime();
                            break;
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "日期spinner出错", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(), adapter_date.getItem(arg2), Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinner_expense.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                expenseFlag=arg2;
                if(expenseFlag==1)expense=true;
                if(expenseFlag==2)expense=false;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_inquire_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void inquire(View view) {
        List<Long> list_id = new ArrayList<>();
        EditText editText = (EditText) findViewById(R.id.text_inquire);
        inquire = editText.getText().toString();
        List<String> list_result = new ArrayList<String>();
        RecordUtils recordUtils = new RecordUtils(this);
        //records = recordUtils.searchRecord(1L, null, null, null, null, null, null, null, null);
        records = recordUtils.searchRecord(1L,startDate,endDate,
                (accountId==-1 ? null : accountId),
                (type==-1 ? null : type),
                (expenseFlag==0 ? null : expense),
                null,null,inquire);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //long i = 1;
        for(int i=0; i<records.size(); i++) {
            String newRecord = " ";
            newRecord += fm.format(records.get(i).getTime());
            newRecord += " ";
            newRecord += records.get(i).getExpense()?"收入 ":"支出 ";
            newRecord += records.get(i).getAmount();
            newRecord += " ";
            switch (records.get(i).getType()){
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
            list_id.add(records.get(i).getId());
        }
        final List<Long> list_idF = list_id;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Inquire.this, android.R.layout.simple_list_item_1, list_result);
        listView = (ListView) findViewById(R.id.listview_result);
        listView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "搜索完成", Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RecordDetail.class);
                intent.putExtra("recordid", list_idF.get(i));//传递流水id
                startActivity(intent);
            }
        });
    }
}
