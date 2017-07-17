package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Book;
import com.cirnoteam.accountingassistant.entity.User;
import com.cirnoteam.accountingassistant.gen.BookDao;
import com.cirnoteam.accountingassistant.gen.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Yifan on 2017/7/15.
 */

public class BookUtils {
    private DaoManager daoManager;
    private Context context;

    public BookUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
        this.context = context;
    }

    public Book getBook(Long id) {
        QueryBuilder<Book> builder = daoManager.getDaoSession().queryBuilder(Book.class);
        return builder.where(BookDao.Properties.Id.eq(id)).unique();
    }

    public List<Book> getAllBooks(String username) {
        QueryBuilder<User> builder = daoManager.getDaoSession().queryBuilder(User.class);
        User user = builder.where(UserDao.Properties.Username.eq(username)).unique();
        return user.getBooks();
    }

    private boolean insertBook(Book book) {
        return daoManager.getDaoSession().insert(book) != -1;
    }

    private boolean updateBook(Book book) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().update(book);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean deleteBook(Book book) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().delete(book);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean addBook(String bookName, String username) {
        Book book = new Book(null, username, bookName, null);
        return insertBook(book);
    }

    public boolean setRemoteId(Long bookId, Long remoteId) {
        Book book = getBook(bookId);
        book.setRemoteid(remoteId);
        return updateBook(book);
    }

    public boolean updateBookName(Long bookId, String newName) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Book book = getBook(bookId);
        book.setName(newName);
        if (!util.addDirty(book, false)) {
            flag = false;
        }
        if (!updateBook(book)) {
            flag = false;
        }
        return flag;
    }

    public boolean deleteBook(Long bookId) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Book book = getBook(bookId);
        if (!util.addDirty(book, true)) {
            flag = false;
        }
        if (!deleteBook(book)) {
            flag = false;
        }
        return flag;
    }

}
