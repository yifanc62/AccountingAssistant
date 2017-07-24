package com.cirnoteam.accountingassistant.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.cirnoteam.accountingassistant.R;
import com.cirnoteam.accountingassistant.database.RecordUtils;
import com.cirnoteam.accountingassistant.entity.*;
import com.cirnoteam.accountingassistant.entity.Record;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pc on 2017/7/20.
 */

public class Chart extends AppCompatActivity {
    private Spinner spinner_date;
    private Spinner spinner_expense;
    private Date startDate, endDate;
    private boolean expense;
    private RecordUtils recordUtils = new RecordUtils(this);
    private List<Record> list_records = new ArrayList<>();
    private List<String> list_date = new ArrayList<>();
    private List<String> list_expense = new ArrayList<>();
    private ArrayAdapter<String> adapter_date;
    private ArrayAdapter<String> adapter_expense;

    String[] typeName = new String[]{
            "一日三餐", "购物消费", "水电煤气", "交通花费", "医疗消费", "其他支出", "经营获利", "工资收入", "路上捡钱", "其他收入"
    };


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        initActionBar();

        list_date.add("全期间");
        list_date.add("本日");
        list_date.add("本周");
        list_date.add("本月");
        list_date.add("本年");
        list_expense.add("支出");
        list_expense.add("收入");
        adapter_date = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_date);
        adapter_expense = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_expense);
        spinner_date = (Spinner) findViewById(R.id.spinner_date);
        spinner_expense = (Spinner) findViewById(R.id.spinner_expense);
        spinner_date.setAdapter(adapter_date);
        spinner_expense.setAdapter(adapter_expense);
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
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "日期spinner出错", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getApplicationContext(), adapter_date.getItem(arg2), Toast.LENGTH_SHORT).show();
                changeChart();//刷新图表
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getApplicationContext(), "none", Toast.LENGTH_SHORT).show();
                arg0.setVisibility(View.VISIBLE);
            }
        });
        spinner_expense.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                expense = arg2 == 0;
                changeChart();//刷新图表
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });

        changeChart();//刷新图表
        Button btn = (Button) findViewById(R.id.change_chart);
        btn.setVisibility(View.INVISIBLE);
        PieChart pieChart = (PieChart) findViewById(R.id.pie_chart);
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        pieChart.startAnimation(scaleAnimation);
    }

    public void changeChart() {
        float[] type = new float[10];
        type = typeDivide(startDate, endDate);


        PieChart pieChart = (PieChart) findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        pieChart.setDescription("流水图表");//设置描述
        pieChart.setDescriptionTextSize(20);//设置描述字体大小
//pieChart.setDescriptionColor(); //设置描述颜色
//pieChart.setDescriptionTypeface();//设置描述字体
        pieChart.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离上下左右的偏移量

        pieChart.setDragDecelerationFrictionCoef(1f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难

        pieChart.setDrawCenterText(true);//是否绘制中间的文字
        pieChart.setCenterTextColor(Color.argb(0xff, 0x87, 0xce, 0xeb));//中间的文字颜色
        pieChart.setCenterTextSize(16);//中间的文字字体大小

        pieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        pieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小

        pieChart.setTransparentCircleColor(Color.BLACK);//设置圆环的颜色
        pieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        pieChart.setTransparentCircleRadius(60f);//设置圆环的半径值

// enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转(默认为true)
        pieChart.setRotationAngle(10);//设置饼状图旋转的角度

        pieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

// entry label styling
        pieChart.setDrawEntryLabels(true);//设置是否绘制Label
        pieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
//pieChart.setEntryLabelTypeface(mTfRegular);
        pieChart.setEntryLabelTextSize(10f);//设置绘制Label的字体大小

        //pieChart.setOnChartValueSelectedListener(this);//设值点击时候的回调
        pieChart.animateY(340, Easing.EasingOption.EaseInQuad);
        //设置Y轴上的绘制动画
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        //for(Record list_record : list_records){
        for (int i = 9; i >= 0; i--) {
            if (type[i] == 0)
                continue;
            PieEntry pieEntry = new PieEntry(type[i], typeName[i]);
            pieEntries.add(pieEntry);
        }
        String centerText1 = "总流水(支出)";
        String centerText2 = "总流水(收入)";
        if (expense)
            pieChart.setCenterText(centerText1);//设置中间的文字
        else
            pieChart.setCenterText(centerText2);//设置中间的文字
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(getPieChartColors());
        pieDataSet.setSliceSpace(3f);//设置选中的Tab离两边的距离
        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLUE);

        pieChart.setData(pieData);
// undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();

    }

    private int[] getPieChartColors() {
        int[] color = new int[]{Color.argb(0xff, 0x9f, 0x79, 0xee), Color.argb(0xff, 0xdc, 0x14, 0x3c), Color.argb(0xff, 0xee, 0xad, 0x0e), Color.argb(0xff, 0x96, 0x96, 0x96), Color.argb(0xff, 0x8d, 0xee, 0xee), Color.argb(0xff, 0x66, 0xcd, 0x00), Color.argb(0xff, 0x1e, 0x90, 0xff), Color.argb(0xff, 0xcd, 0x33, 0x33), Color.argb(0xff, 0xcd, 0xc1, 0xc5), Color.argb(0xff, 0xff, 0x8c, 0x69)};
        return color;
    }

    public float[] typeDivide(Date startDate, Date endDate) {
        float[] type = new float[10];
        for (int i = 9; i >= 0; i--) {
            list_records = recordUtils.searchRecord(Status.bookid, startDate, endDate,
                    null,
                    i,
                    expense,
                    null, null, null);
            for (Record list_record : list_records)
                type[i] += list_record.getAmount();
        }
        return type;
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_chart_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
