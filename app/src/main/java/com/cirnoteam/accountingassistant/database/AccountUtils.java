package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.gen.AccountDao;
import com.cirnoteam.accountingassistant.gen.BookDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
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
        //查询构建器
        QueryBuilder<Account> queryBuilder = daoManager.getDaoSession().queryBuilder(Account.class);
        List<Account> list = queryBuilder.list();
        return list;
    }

    public boolean insertAccount(Account account) {
        return daoManager.getDaoSession().insert(account) != -1;
    }

    public Account getAccount(long id) {
        QueryBuilder<Account> builder = daoManager.getDaoSession().queryBuilder(Account.class).where(AccountDao.Properties.Id.eq(id));
        return builder.unique();
    }
    public boolean deleteAccount(Account account) {
        boolean flag = false;
        try {
            //删除指定ID
            daoManager.getDaoSession().delete(account);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //daoManager.getDaoSession().deleteAll(); //删除所有记录
        return flag;
    }
}
