package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Book;
import com.cirnoteam.accountingassistant.entity.User;
import com.cirnoteam.accountingassistant.gen.BookDao;
import com.cirnoteam.accountingassistant.gen.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by Yifan on 2017/7/15.
 */

public class BookUtils {
    private DaoManager daoManager;

    public BookUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
    }

    public Book getBook(Long id) {
        QueryBuilder<Book> builder = daoManager.getDaoSession().queryBuilder(Book.class);
        return builder.where(BookDao.Properties.Id.eq(id)).unique();
    }

}
