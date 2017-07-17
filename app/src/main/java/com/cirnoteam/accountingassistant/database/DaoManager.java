package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.gen.DaoMaster;
import com.cirnoteam.accountingassistant.gen.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by Yifan on 2017/7/15.
 */

public class DaoManager {
    //TAG
    public static final String TAG = DaoManager.class.getSimpleName();
    //数据库名称
    private static final String DB_NAME = "data.db";
    //多线程访问
    private volatile static DaoManager manager;
    //操作类
    private static DaoMaster.DevOpenHelper helper;
    //上下文
    private Context mContext;
    //核心类
    private static DaoMaster daoMaster;
    private DaoSession daoSession;

    //单例模式
//    public static DaoManager getInstance() {
//        DaoManager instance = null;
//        if (manager == null) {
//            synchronized (DaoManager.class) {
//                if (instance == null) {
//                    instance = new DaoManager();
//                    manager = instance;
//                }
//            }
//        }
//        return instance;
//    }
    public static DaoManager getInstance() {
        if (manager == null) {
            synchronized (DaoManager.class) {
                if (manager == null) {
                    manager = new DaoManager();
                }
            }
        }
        return manager;
    }

    //传递上下文
    public void initManager(Context context) {
        this.mContext = context;
    }

    /**
     * 判断是否存在数据库，如果没有则创建
     *
     * @return
     */
    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            //Fixme:DevOpenHelper->OpenHelper
            helper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 完成对数据库的操作，只是个接口
     *
     * @return
     */
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug(boolean debug) {
        QueryBuilder.LOG_SQL = debug;
        QueryBuilder.LOG_VALUES = debug;
    }

    /**
     * 关闭DaoSession
     */
    public void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    /**
     * 关闭Helper
     */
    public void closeHelper() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

    /**
     * 关闭所有的操作
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }
}
