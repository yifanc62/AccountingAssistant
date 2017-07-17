package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.gen.BookDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 * Created by Yifan on 2017/7/15.
 */

public class AccountUtils {
    private DaoManager daoManager;

    public AccountUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
    }

    public List<Account> queryBuilder() {
        QueryBuilder<Account> queryBuilder = daoManager.getDaoSession().queryBuilder(Account.class);
        List<Account> list = queryBuilder.list();
        return list;
    }

    public boolean insertAccount(Account account) {
        return daoManager.getDaoSession().insert(account) != -1;
    }

//    public List<Account> getTypeName() {
//        //查询构建器
//        QueryBuilder<Account> queryBuilder = daoManager.getDaoSession().queryBuilder(Account.class);
//        List<Account> list = queryBuilder.list();
//
//        Account account = new Account();
//        if(account.getType().equals(0))
//
//        return list;
//    }
}
