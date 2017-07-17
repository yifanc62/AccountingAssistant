package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Record;
import com.cirnoteam.accountingassistant.entity.User;
import com.cirnoteam.accountingassistant.gen.RecordDao;
import com.cirnoteam.accountingassistant.gen.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.cirnoteam.accountingassistant.database.DaoManager.TAG;

/**
 * RecordUtils
 * 流水操作类
 *
 * @author Yifan
 * @version 0.8
 */

public class RecordUtils {
    private DaoManager daoManager;

    /**
     * 构造方法
     *
     * @param context 当前App上下文
     */
    public RecordUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
    }

    /**
     * 查询某天的流水
     */
    public List<Record> ReadRecordOfDay(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DAY_OF_MONTH, 1);
        Date date1 = gc.getTime();
        QueryBuilder<Record> queryBuilder = daoManager.getDaoSession().queryBuilder(Record.class);
        return queryBuilder.where(RecordDao.Properties.Time.between(date, date1)).list();
    }

    /**
     * 用id查询唯一流水
     */
    public Record ReadRecordById(int id) {
        QueryBuilder<Record> queryBuilder = daoManager.getDaoSession().queryBuilder(Record.class);
        List<Record> list = queryBuilder.where(RecordDao.Properties.Id.eq(id)).list();
        return list.get(0);
    }

    /**
     * 插入流水
     */
    public boolean insertRecord(Record record) {
        return daoManager.getDaoSession().insert(record) != -1;
    }
}
