package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.entity.Book;
import com.cirnoteam.accountingassistant.entity.Dirty;
import com.cirnoteam.accountingassistant.entity.Record;
import com.cirnoteam.accountingassistant.gen.DirtyDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DirtyUtils
 * 脏数据操作类
 *
 * @author Yifan
 * @version 1.2
 */

public class DirtyUtils {
    private DaoManager daoManager;
    private Context context;
    private int TYPE_BOOK = 2;
    private int TYPE_ACCOUNT = 1;
    private int TYPE_RECORD = 0;

    public DirtyUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
        this.context = context;
    }

    private boolean insertDirty(Dirty dirty) {
        return daoManager.getDaoSession().insert(dirty) != -1;
    }

    private boolean updateDirty(Dirty dirty) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().update(dirty);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean deleteDirty(Dirty dirty) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().delete(dirty);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private Dirty getDirty(Long rid, Integer type) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        return builder.where(DirtyDao.Properties.Rid.eq(rid)).where(DirtyDao.Properties.Type.eq(type)).unique();
    }

    private boolean isDirtyExists(Long rid, Integer type) {
        return getDirty(rid, type) != null;
    }

    public boolean deleteAllDirty(String username) {
        boolean flag = true;
        for (Dirty dirty : getAllDirties(username)) {
            if (!deleteDirty(dirty)) {
                flag = false;
            }
        }
        return flag;
    }

    public boolean addDirty(Book book, Boolean deleted) {
        Long rid = book.getId();
        int type = TYPE_BOOK;
        if (deleted) {
            boolean flag = true;
            if (book.getRemoteid() != null) {
                if (isDirtyExists(rid, type)) {
                    Dirty dirty = getDirty(rid, type);
                    dirty.setDeleted(true);
                    dirty.setTime(new Date());
                    if (!updateDirty(dirty)) {
                        flag = false;
                    }
                    for (Account account : book.getAccounts()) {
                        if (!addDirty(account, true)) {
                            flag = false;
                        }
                    }
                    return flag;
                } else {
                    if (!insertDirty(new Dirty(null, book.getUsername(), rid, type, book.getRemoteid(), true, new Date()))) {
                        flag = false;
                    }
                    for (Account account : book.getAccounts()) {
                        if (!addDirty(account, true)) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            } else {
                if (!deleteDirty(getDirty(rid, type))) {
                    flag = false;
                }
                for (Account account : book.getAccounts()) {
                    if (!addDirty(account, true)) {
                        flag = false;
                    }
                }
                return flag;
            }
        } else {
            if (isDirtyExists(rid, type)) {
                Dirty dirty = getDirty(rid, type);
                dirty.setTime(new Date());
                return updateDirty(dirty);
            } else {
                return insertDirty(new Dirty(null, book.getUsername(), rid, type, book.getRemoteid(), false, new Date()));
            }
        }
    }

    public boolean addDirty(Account account, Boolean deleted) {
        Long rid = account.getId();
        int type = TYPE_ACCOUNT;
        if (deleted) {
            boolean flag = true;
            if (account.getRemoteid() != null) {
                if (isDirtyExists(rid, type)) {
                    Dirty dirty = getDirty(rid, type);
                    dirty.setDeleted(true);
                    dirty.setTime(new Date());
                    if (!updateDirty(dirty)) {
                        flag = false;
                    }
                    for (Record record : account.getRecords()) {
                        if (!addDirty(record, true)) {
                            flag = false;
                        }
                    }
                    return flag;
                } else {
                    if (!insertDirty(new Dirty(null, account.getBook().getUsername(), rid, type, account.getRemoteid(), true, new Date()))) {
                        flag = false;
                    }
                    for (Record record : account.getRecords()) {
                        if (!addDirty(record, true)) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            } else {
                if (!deleteDirty(getDirty(rid, type))) {
                    flag = false;
                }
                for (Record record : account.getRecords()) {
                    if (!addDirty(record, true)) {
                        flag = false;
                    }
                }
                return flag;
            }
        } else {
            if (isDirtyExists(rid, type)) {
                Dirty dirty = getDirty(rid, type);
                dirty.setTime(new Date());
                return updateDirty(dirty);
            } else {
                return insertDirty(new Dirty(null, account.getBook().getUsername(), rid, type, account.getRemoteid(), false, new Date()));
            }
        }
    }

    public boolean addDirty(Record record, Boolean deleted) {
        Long rid = record.getId();
        int type = TYPE_RECORD;
        if (deleted) {
            if (record.getRemoteid() != null) {
                if (isDirtyExists(rid, type)) {
                    Dirty dirty = getDirty(rid, type);
                    dirty.setDeleted(true);
                    dirty.setTime(new Date());
                    return updateDirty(dirty);
                } else {
                    return insertDirty(new Dirty(null, record.getAccount().getBook().getUsername(), rid, type, record.getRemoteid(), true, new Date()));
                }
            } else {
                return deleteDirty(getDirty(rid, type));
            }
        } else {
            if (isDirtyExists(rid, type)) {
                Dirty dirty = getDirty(rid, type);
                dirty.setTime(new Date());
                return updateDirty(dirty);
            } else {
                return insertDirty(new Dirty(null, record.getAccount().getBook().getUsername(), rid, type, record.getRemoteid(), false, new Date()));
            }
        }
    }

    public List<Dirty> getAllDirties(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        return builder.where(DirtyDao.Properties.Username.eq(username)).list();
    }

    public List<Book> getAllModifiedBooks(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_BOOK)).where(DirtyDao.Properties.Deleted.eq(false)).where(DirtyDao.Properties.Remoteid.isNotNull()).list();
        List<Book> books = new ArrayList<>();
        BookUtils util = new BookUtils(context);
        for (Dirty dirty : dirties) {
            books.add(util.getBook(dirty.getRid()));
        }
        return books;
    }

    public List<Account> getAllModifiedAccounts(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_ACCOUNT)).where(DirtyDao.Properties.Deleted.eq(false)).where(DirtyDao.Properties.Remoteid.isNotNull()).list();
        List<Account> accounts = new ArrayList<>();
        AccountUtils util = new AccountUtils(context);
        for (Dirty dirty : dirties) {
            accounts.add(util.getAccount(dirty.getRid()));
        }
        return accounts;
    }

    public List<Record> getAllModifiedRecords(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_RECORD)).where(DirtyDao.Properties.Deleted.eq(false)).where(DirtyDao.Properties.Remoteid.isNotNull()).list();
        List<Record> records = new ArrayList<>();
        RecordUtils util = new RecordUtils(context);
        for (Dirty dirty : dirties) {
            records.add(util.getRecord(dirty.getRid()));
        }
        return records;
    }

    public List<Book> getAllNewAddedBooks(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_BOOK)).where(DirtyDao.Properties.Remoteid.isNull()).list();
        List<Book> books = new ArrayList<>();
        BookUtils util = new BookUtils(context);
        for (Dirty dirty : dirties) {
            books.add(util.getBook(dirty.getRid()));
        }
        return books;
    }

    public List<Account> getAllNewAddedAccounts(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_ACCOUNT)).where(DirtyDao.Properties.Remoteid.isNull()).list();
        List<Account> accounts = new ArrayList<>();
        AccountUtils util = new AccountUtils(context);
        for (Dirty dirty : dirties) {
            accounts.add(util.getAccount(dirty.getRid()));
        }
        return accounts;
    }

    public List<Record> getAllNewAddedRecords(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_RECORD)).where(DirtyDao.Properties.Remoteid.isNull()).list();
        List<Record> records = new ArrayList<>();
        RecordUtils util = new RecordUtils(context);
        for (Dirty dirty : dirties) {
            records.add(util.getRecord(dirty.getRid()));
        }
        return records;
    }

    public List<Long> getAllDeletedBooksRemoteIds(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_BOOK)).where(DirtyDao.Properties.Deleted.eq(true)).list();
        List<Long> ids = new ArrayList<>();
        for (Dirty dirty : dirties) {
            ids.add(dirty.getRemoteid());
        }
        return ids;
    }

    public List<Long> getAllDeletedAccountsRemoteIds(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_ACCOUNT)).where(DirtyDao.Properties.Deleted.eq(true)).list();
        List<Long> ids = new ArrayList<>();
        for (Dirty dirty : dirties) {
            ids.add(dirty.getRemoteid());
        }
        return ids;
    }

    public List<Long> getAllDeletedRecordsRemoteIds(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        List<Dirty> dirties = builder.where(DirtyDao.Properties.Username.eq(username)).where(DirtyDao.Properties.Type.eq(TYPE_RECORD)).where(DirtyDao.Properties.Deleted.eq(true)).list();
        List<Long> ids = new ArrayList<>();
        for (Dirty dirty : dirties) {
            ids.add(dirty.getRemoteid());
        }
        return ids;
    }
}
