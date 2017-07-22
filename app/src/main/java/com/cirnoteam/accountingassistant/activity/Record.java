package com.cirnoteam.accountingassistant.activity;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.RecordUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.PendingIntent.getActivity;

/**
 * Created by pc on 2017/7/12.
 */

public class Record extends AppCompatActivity {
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
                    return true;
                case R.id.navigation_user:
                    Intent intentToUser = new Intent(Record.this, BankCard.class);
                    startActivity(intentToUser);
                    finish();
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
    }
    @Override
    protected void onResume() {
        super.onResume();

        //获取当月所有日期数组
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.MONTH, -Status.month);
        cal.set(Calendar.DAY_OF_MONTH, 1);//获取第一天
        Date currentDate = cal.getTime();//日期指针
        int numOfDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);//当月天数
        String[] day = new String[numOfDay];//天
        //ArrayList<>[] record = new ArrayList<String>[numOfDay];
        final List<List<String>> record = new ArrayList<>();//每一天的记录内容
        List<List<Long>> recordid = new ArrayList<>();//每一天的记录id
        String recordStatement = " ";
        List<com.cirnoteam.accountingassistant.entity.Record> list;
        RecordUtils u = new RecordUtils(this);
        for(int i=1 ; i<=numOfDay ; i++){
            record.add(new ArrayList<String>());
            recordid.add(new ArrayList<Long>());
            day[i-1] = fm.format(currentDate);
            list = u.getRecordOfDayByBook(Status.bookid,currentDate);//TODO 需要getRecordOfDayByBook
            for(int j=0;j<list.size();j++){
                recordStatement = " ";
                recordStatement += list.get(j).getExpense() ? "支出" : "收入";
                recordStatement += " " + String.valueOf(list.get(j).getAmount());
                recordStatement += " " + list.get(j).getType();
                switch (list.get(j).getType()){
                    case 0:recordStatement += " 一日三餐";
                        break;
                    case 1:recordStatement += " 购物消费";
                        break;
                    case 2:recordStatement += " 水电煤气";
                        break;
                    case 3:recordStatement += " 交通花费";
                        break;
                    case 4:recordStatement += " 医疗消费";
                        break;
                    case 5:recordStatement += " 其他支出";
                        break;
                    case 6:recordStatement += " 经营获利";
                        break;
                    case 7:recordStatement += " 工资收入";
                        break;
                    case 8:recordStatement += " 路上捡钱";
                        break;
                    case 9:recordStatement += " 其他收入";
                        break;
                }
                record.get(i-1).add(recordStatement);
                recordid.get(i-1).add(list.get(j).getId());
            }
            cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH)+1);//让日期加1
            currentDate = cal.getTime();
        }
        final String[] dayF = day;
        final List<List<String>> recordF = record;//转换为final
        final List<List<Long>> recordidF = recordid;

        initActionBar();
        Spinner dateSpinner = (Spinner)findViewById(R.id.spinner_date);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getSpinnerItems(Calendar.getInstance()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setSelection(Status.month);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_record);

        ExpandableListAdapter adapter_2 = new ExpandableListAdapter() {
            int [] logos = new int[]{
                    R.drawable.ic_dashboard_black_24dp,
                    R.drawable.ic_dashboard_black_24dp,
                    R.drawable.ic_dashboard_black_24dp
            };
            private String[] armType = dayF;
            private List<List<String>> arms = recordF;
            public void setArmType(String[] str){
                armType = str;
            }
            public void setArms(List<List<String>> list){
                arms = list;
            }
            //private String[][] arms = {{"o","p","q"},{"r","s","t"},{"1","3","5","10"}};
            private TextView getTextView(){
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(Record.this);
                textView.setLayoutParams(lp);
                textView.setPadding(50,50,0,50);
                textView.setTextSize(20);
                return textView;
            }
            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getGroupCount() {
                return armType.length;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return arms.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return armType[groupPosition];
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return arms.get(groupPosition).get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                LinearLayout ll = new LinearLayout(Record.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ImageView logo = new ImageView(Record.this);
                //logo.setImageResource(logos[groupPosition]);
                //ll.addView(logo);
                TextView textView = getTextView();
                textView.setText(getGroup(groupPosition).toString());
                ll.addView(textView);
                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                TextView textView = getTextView();
                textView.setText(getChild(groupPosition,childPosition).toString());
                return textView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public void onGroupExpanded(int i) {

            }

            @Override
            public void onGroupCollapsed(int i) {

            }

            @Override
            public long getCombinedChildId(long l, long l1) {
                return 0;
            }

            @Override
            public long getCombinedGroupId(long l) {
                return 0;
            }
        };


        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getApplicationContext(),RecordDetail.class);
                intent.putExtra("recordid", recordidF.get(groupPosition).get(childPosition));
                intent.putExtra("backpage", 0);//标记返回界面
                startActivity(intent);
                return true;
            }
        });

        expandableListView.setAdapter(adapter_2);

        /*******dateSpinner监听*******/
        dateSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                arg0.setVisibility(View.VISIBLE);
                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM");
                if (Status.month != arg2) {
                    Status.month = arg2;
                    Intent intent = new Intent(getApplicationContext(), Record.class);
                    startActivity(intent);
                    finish();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_record_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private List<String> getSpinnerItems(Calendar now) {
        int y = now.get(Calendar.YEAR);
        int m = now.get(Calendar.MONTH);
        List<String> result = new ArrayList<>();
        if (y >= 2015) {
            //result.add(String.format("%04d",y));
            for (int i = m; i >= 0; i--) {
                result.add(String.format("%04d-%02d", y, i + 1));
            }
        }
        y--;
        for (int i = y; i > 2014; i--) {
            //result.add(String.format("%04d",i));
            for (int j = 12; j > 0; j--) {
                result.add(String.format("%04d-%02d", i, j));
            }
        }
        return result;
    }


    public void toInquire(View view) {
        Intent intent = new Intent(this, Inquire.class);
        startActivity(intent);
    }

}
