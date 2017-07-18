package com.cirnoteam.accountingassistant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    //    private String[] bankcardnames = new String[]
//            {"支付宝1","银行卡1","现金1"};
//
//    private String[] bankcardnumbers = new String[]
//            {"123", "456", "789",};
    private List<Account> accounts = new ArrayList<Account>();
    public long item;

    public void toNewBankcard(View view) {
        Intent intent = new Intent(this, NewBankcard.class);
        startActivity(intent);
        finish();
    }

//    public void toBankcard(View view) {
//        Intent intent = new Intent(this, BankCard.class);
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_card);
        //String[] bankcardnames = ReadDB.readAccount(this.getFilesDir().toString());
        AccountUtils accountUtils = new AccountUtils(this);
        //TODO 修改获得的ID
        accounts = accountUtils.getAllAccounts(0L);
        //创建一个List集合，集合的元素是Map
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_user);
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        long[] map = new long[accounts.size()];
        int i = 0;
        for (Account account : accounts) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("bankcardname", account.getName());
            listItem.put("bankcardnumber", getTypeName(account));
            listItems.add(listItem);
            map[i] = accounts.get(i).getId();
            i++;
        }
        final long[] finalMap = map;

        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.simple_item,
                new String[]{"bankcardname", "bankcardnumber"},
                new int[]{R.id.bankcardname, R.id.bankcardnumber});
        ListView list = (ListView) findViewById(R.id.BankCardListView);
        //为ListView设置Adapter
        list.setAdapter(simpleAdapter);

        final AlertDialog delete = new AlertDialog.Builder(this).create();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete.show();
                item = finalMap[i];
                Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
        });
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        deleteAccountRecord(item);
                        Intent intent = new Intent(getApplicationContext(), BankCard.class);
                        startActivity(intent);
                        finish();
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
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


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentToMain = new Intent(BankCard.this, MainActivity.class);
                    intentToMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToMain);
                    return true;
                case R.id.navigation_record:
                    Intent intentToRecord = new Intent(BankCard.this, Record.class);
                    intentToRecord.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToRecord);
                    return true;
                case R.id.navigation_user:

                    return true;
            }
            return false;

        }

    };

    public void deleteAccountRecord(long id) {
        AccountUtils accountUtils = new AccountUtils(this);
        accountUtils.deleteAccount(accountUtils.getAccount(id));

        //Todo:dirty table
    }

    public String getTypeName(Account account) {
        String string = new String();
        if (account.getType().equals(0))
            string = "支付宝";
        if (account.getType().equals(1))
            string = "银行卡";
        if (account.getType().equals(2))
            string = "现金";
        return string;
    }
}
