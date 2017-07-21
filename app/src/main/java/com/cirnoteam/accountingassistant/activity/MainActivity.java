//嘻嘻
package com.cirnoteam.accountingassistant.activity;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.AccountUtils;
import com.cirnoteam.accountingassistant.database.BookUtils;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.database.UserUtils;
import com.cirnoteam.accountingassistant.entity.Book;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{;

    private List<String> bookName = new ArrayList<>();
    private List<String> record = new ArrayList<>();
    private List<Book> list_books = new ArrayList<>();
    private List<Long> list_book_id = new ArrayList<>();
    private List<Long> list_record_id = new ArrayList<>();
    private ImageButton leftmenu;
    private TextView mDate;
    private Spinner mySpinner;
    private ArrayAdapter<String> bookAdapter;
    private ListView myListView;
    private ArrayAdapter<String> recordAdapter;
    private List<com.cirnoteam.accountingassistant.entity.Record> records = new ArrayList<com.cirnoteam.accountingassistant.entity.Record>();
    //private Book book = new Book();
    private BookUtils bookUtils = new BookUtils(this);
    private UserUtils userUtils = new UserUtils(this);
    private AccountUtils accountUtils = new AccountUtils(this);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_record:
                    Intent intentToRecord = new Intent(MainActivity.this, Record.class);
                    intentToRecord.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToRecord);
                    return true;
                case R.id.navigation_user:
                    Intent intentToUser = new Intent(MainActivity.this, BankCard.class);
                    intentToUser.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentToUser);
                    return true;
            }
            return false;

        }

    };

    private void showInputDialog() {
        final EditText editText = new EditText(MainActivity.this);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);

        inputDialog.setTitle("请输入账本名称").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bookAdapter.insert(editText.getText().toString(),0);
                        Long id;
                        if((id = bookUtils.addBook(userUtils.getCurrentUsername(),editText.getText().toString()))!=-1) {
                            accountUtils.addAccount(id,0,1000F,"默认账户");
                            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            Status.bookid = id;
                            reSetSpinner();
                            for(int i=0;i<list_book_id.size();i++) {
                                if(list_book_id.get(i) == id)
                                    mySpinner.setSelection(i);
                            }
                        }
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //mySpinner.setSelection(bookAdapter.getPosition(bookUtils.getBook(Status.bookid).getName()));
                        //把spinner的当前选项还原为正确选项
                        //do nothing
                    }
                });
        inputDialog.show();
    }
    private void showDeleteDialog() {
        final EditText editText = new EditText(MainActivity.this);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);

        inputDialog.setTitle("是否删除当前账本");
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(bookUtils.deleteBook(Status.bookid))
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        reSetSpinner();
                        mySpinner.setSelection(0);
                        Status.bookid = list_book_id.get(0);
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
        inputDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        Calendar cal = Calendar.getInstance();
        String currentDate = cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH);
        mDate = (TextView) findViewById(R.id.Date);
        mDate.setText(currentDate);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftmenu = (ImageButton) findViewById(R.id.user);
        leftmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });

        //spinner
        mySpinner = (Spinner)findViewById(R.id.spinner_book);
        BookUtils u = new BookUtils(this);
        list_books = u.getAllBooks(userUtils.getCurrentUsername());
        for(Book list_book:list_books){
            bookName.add(list_book.getName());
            list_book_id.add(list_book.getId());
        }
        Status.bookid = list_book_id.get(0);
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookName);

        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(bookAdapter);
        //bookAdapter.add("默认账本");
        bookAdapter.add("＋");

        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == (bookAdapter.getCount()-1)) {
                    showInputDialog();
                    for(int i=0;i<list_book_id.size();i++) {
                        if(list_book_id.get(i) == Status.bookid)
                            mySpinner.setSelection(i);
                    }//还原spinner选项
                    arg0.setVisibility(View.VISIBLE);
                } else{
                    Status.bookid = list_book_id.get(arg2);
                    reSetList();//调用设值方法以刷新
                }
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });

        //list
        reSetList();
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RecordDetail.class);
                intent.putExtra("recordid", list_record_id.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        reSetSpinner();//调用设spinner值方法
        if(Status.bookid != null) {
            for(int i=0;i<list_book_id.size();i++) {
                if(list_book_id.get(i) == Status.bookid)
                    mySpinner.setSelection(i);
            }
        }

        reSetList();//调用设流水列表值方法

        Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
    }

    private void reSetList(){//刷新、设置list的值
        list_record_id.clear();
        record.clear();
        RecordUtils recordUtils = new RecordUtils(this);
        //records = recordUtils.getLateast4Records(Status.bookid);
        records = recordUtils.getLateast4Records(Status.bookid);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        int i = 0;
        for(com.cirnoteam.accountingassistant.entity.Record _record : records) {
            String newRecord = " ";
            newRecord += fm.format(_record.getTime());
            newRecord += " ";
            newRecord += _record.getExpense()?"收入 ":"支出 ";
            newRecord += _record.getAmount();
            newRecord += " ";
            switch (_record.getType()){
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
            list_record_id.add(_record.getId());
            record.add(newRecord);
            i++;
            if(i == 4)
                break;
        }
        myListView = (ListView) findViewById(R.id.listview);
        myListView.setDividerHeight(5);
        recordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, record);
        myListView.setAdapter(recordAdapter);
    }

    private void reSetSpinner(){
        list_book_id.clear();
        bookName.clear();

//        第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list
        BookUtils u = new BookUtils(this);
        list_books = u.getAllBooks(userUtils.getCurrentUsername());
        for(Book list_book:list_books){
            bookName.add(list_book.getName());
            list_book_id.add(list_book.getId());
        }
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bookName);

        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(bookAdapter);
        //bookAdapter.add("默认账本");
        bookAdapter.add("＋");
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        //((BaseAdapter) mySpinner.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toLogIn(View view) {
        Intent intent = new Intent(this, Record.class);
        startActivity(intent);
    }

    public void toNewRecord(View view) {
        Intent intent = new Intent(this, NewRecord.class);
        startActivity(intent);
    }

    public void toInquire(View view) {
        Intent intent = new Intent(this, Inquire.class);
        startActivity(intent);
    }
    public void toChart(View view){
        Intent intent = new Intent(this,Chart.class);
        startActivity(intent);
    }

    public void toDeleteBook(View view){
        showDeleteDialog();
    }
}
