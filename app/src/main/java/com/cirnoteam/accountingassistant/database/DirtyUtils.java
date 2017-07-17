package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.entity.Book;
import com.cirnoteam.accountingassistant.entity.Dirty;
import com.cirnoteam.accountingassistant.entity.Record;
import com.cirnoteam.accountingassistant.gen.DirtyDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by Yifan on 2017/7/15.
 */

public class DirtyUtils {
    private DaoManager daoManager;

    public DirtyUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
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

    private Dirty getDirty(Long rid, int type) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        return builder.where(DirtyDao.Properties.Rid.eq(rid)).where(DirtyDao.Properties.Type.eq(type)).unique();
    }

    private boolean isDirtyExists(Long rid, int type) {
        return getDirty(rid, type) != null;
    }

    public List<Dirty> getAllDirties(String username) {
        QueryBuilder<Dirty> builder = daoManager.getDaoSession().queryBuilder(Dirty.class);
        return builder.where(DirtyDao.Properties.Username.eq(username)).list();
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

    public boolean addDirty(Book book, boolean deleted) {
        Long rid = book.getId();
        int type = 2;
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

    public boolean addDirty(Account account, boolean deleted) {
        Long rid = account.getId();
        int type = 1;
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

    public boolean addDirty(Record record, boolean deleted) {
        Long rid = record.getId();
        int type = 0;
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
}
