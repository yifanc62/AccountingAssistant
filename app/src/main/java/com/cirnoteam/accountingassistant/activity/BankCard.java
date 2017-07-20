package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.entity.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by tanzi on 2017/7/13.
 */

public class BankCard extends AppCompatActivity {

    private List<Account> accounts = new ArrayList<Account>();
    public long item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_card);
        AccountUtils accountUtils = new AccountUtils(this);
        accounts = accountUtils.getAllAccounts(Status.bookid);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_user);
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        long[] map = new long[accounts.size()];
        int i = 0;
        for(Account account:accounts){
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("accountNumber", account.getName());
            listItem.put("accountName", getTypeName(account)+" 余额:"+account.getBalance());
            listItems.add(listItem);
            map[i] = accounts.get(i).getId();
            i++;
        }
        final long[] finalMap = map;

        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.simple_item,
                new String[]{"accountNumber", "accountName"},
                new int[]{R.id.bankcardname, R.id.bankcardnumber});
        ListView list = (ListView) findViewById(R.id.BankCardListView);
        //为ListView设置Adapter
        list.setAdapter(simpleAdapter);

        final AlertDialog delete = new AlertDialog.Builder(this).create();
        final AlertDialog errorDelete = new AlertDialog.Builder(this).create();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    errorDelete.show();
                else {
                    delete.show();
                    item = finalMap[i];
                    Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();
                }
            }
        });
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        deleteAccountRecord(item);
                        Intent intent = new Intent(getApplicationContext(),BankCard.class);
                        startActivity(intent);
                        finish();
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        DialogInterface.OnClickListener listener_2 = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        delete.setTitle("删除数据");
        delete.setMessage("是否要删除这一行的数据？");
        delete.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener);
        delete.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                listener);
        errorDelete.setTitle("不可删除");
        errorDelete.setMessage("该账户为默认账户，不可删除");
        errorDelete.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                listener_2);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_record:
                    Intent intentToRecord = new Intent(BankCard.this, Record.class);
                    startActivity(intentToRecord);
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_user:

                    return true;
            }
            return false;

        }

    };


    public void deleteAccountRecord(long id){
        AccountUtils accountUtils = new AccountUtils(this);
        accountUtils.deleteAccount(id);

        //Todo:dirty table
    }

    public String getTypeName(Account account){
        switch (account.getType()){
        case 0:
        return "现金";
        case 1:
        return "银行卡";
        case 2:
        return "支付宝";
        case 3:
        return "QQ钱包";
        case 4:
        return "微信";
        case 5:
        return "余额宝";
        case 6:
        return "交通卡";
        case 7:
        return "储值卡";
        case 8:
        return "校园卡";
        default:
        return "其他账户";
        }
    }
    public void toNewBankcard(View view) {
        Intent intent = new Intent(this, NewBankcard.class);
        startActivity(intent);
    }
}