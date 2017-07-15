package com.cirnoteam.accountingassistant.database;

import android.content.Context;

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
}
