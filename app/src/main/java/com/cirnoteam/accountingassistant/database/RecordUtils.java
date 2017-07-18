package com.cirnoteam.accountingassistant.database;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.entity.Record;
import com.cirnoteam.accountingassistant.gen.RecordDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * RecordUtils
 * 流水操作类
 *
 * @author Yifan
 * @version 1.2
 */

public class RecordUtils {
    private DaoManager daoManager;
    private Context context;
    private Comparator<Record> recordComparator = new Comparator<Record>() {
        @Override
        public int compare(Record r1, Record r2) {
            return r1.getTime().compareTo(r2.getTime());
        }
    };

    public static final int TYPE_一日三餐 = 0;
    public static final int TYPE_购物消费 = 1;
    public static final int TYPE_水电煤气 = 2;
    public static final int TYPE_交通花费 = 3;
    public static final int TYPE_医疗消费 = 4;
    public static final int TYPE_其他支出 = 5;
    public static final int TYPE_经营获利 = 6;
    public static final int TYPE_工资收入 = 7;
    public static final int TYPE_路上捡钱 = 8;
    public static final int TYPE_其他收入 = 9;

    public RecordUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
        this.context = context;
    }

    private boolean insertRecord(Record record) {
        return daoManager.getDaoSession().insert(record) != -1;
    }

    private boolean updateRecord(Record record) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().update(record);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean deleteRecord(Record record) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().delete(record);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public Record getRecord(Long recordId) {
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        return builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Id.eq(recordId)).unique();
    }

    public List<Record> getAllRecordsByAccount(Long accountId) {
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        return builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Accountid.eq(accountId)).list();
    }

    public List<Record> getAllRecordsByBook(Long bookId) {
        AccountUtils util = new AccountUtils(context);
        List<Record> records = new ArrayList<>();
        for (Account account : util.getAllAccounts(bookId)) {
            records.addAll(account.getRecords());
        }
        Collections.sort(records, recordComparator);
        return records;
    }

    public List<Record> searchRecord(Long bookId, Date startDate, Date endDate, Long accountId, Integer type, Boolean expense, Float amount, Boolean greaterThan, String searchText) {
        BookUtils util = new BookUtils(context);
        List<Account> accounts = util.getBook(bookId).getAccounts();
        List<Long> accountIds = new ArrayList<>();
        for (Account account : accounts) {
            accountIds.add(account.getId());
        }
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        builder = builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Accountid.in(accountIds));
        if (startDate != null) {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(startDate);
            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
            calendarStart.set(Calendar.MINUTE, 0);
            calendarStart.set(Calendar.SECOND, 0);
            startDate = calendarStart.getTime();
            if (endDate != null) {
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(endDate);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
                calendarEnd.set(Calendar.MINUTE, 0);
                calendarEnd.set(Calendar.SECOND, 0);
                endDate = calendarEnd.getTime();
                builder = builder.where(RecordDao.Properties.Time.between(startDate, endDate));
            } else {
                endDate = new Date();
                builder = builder.where(RecordDao.Properties.Time.between(startDate, endDate));
            }
        }
        if (accountId != null) {
            builder = builder.where(RecordDao.Properties.Accountid.eq(accountId));
        }
        if (type != null) {
            builder = builder.where(RecordDao.Properties.Type.eq(type));
        }
        if (expense != null) {
            builder = builder.where(RecordDao.Properties.Expense.eq(expense));
        }
        if (amount != null && greaterThan != null) {
            if (greaterThan) {
                builder = builder.where(RecordDao.Properties.Amount.ge(amount));
            } else {
                builder = builder.where(RecordDao.Properties.Amount.le(amount));
            }
        }
        if (searchText != null) {
            builder = builder.where(RecordDao.Properties.Remark.like("%" + searchText + "%"));
        }
        return builder.list();
    }

    public List<Record> getRecordOfDayByAccount(Long accountId, Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = gc.getTime();
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        return builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Accountid.eq(accountId)).where(RecordDao.Properties.Time.between(lastDate, date)).list();
    }

    public List<Record> getRecordOfDayByBook(Long bookId, Date date) {
        AccountUtils util = new AccountUtils(context);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = gc.getTime();
        List<Record> records = new ArrayList<>();
        for (Account account : util.getAllAccounts(bookId)) {
            for (Record record : daoManager.getDaoSession().queryBuilder(Record.class).where(RecordDao.Properties.Accountid.eq(account.getId())).where(RecordDao.Properties.Time.between(lastDate, date)).list()) {
                records.add(record);
            }
        }
        Collections.sort(records, recordComparator);
        return records;
    }

    public List<Record> getRecordOfTypeByAccount(Long accountId, Integer type) {
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        return builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Accountid.eq(accountId)).where(RecordDao.Properties.Type.eq(type)).list();
    }

    public List<Record> getRecordOfTypeByBook(Long bookId, Integer type) {
        AccountUtils util = new AccountUtils(context);
        List<Record> records = new ArrayList<>();
        for (Account account : util.getAllAccounts(bookId)) {
            for (Record record : daoManager.getDaoSession().queryBuilder(Record.class).where(RecordDao.Properties.Accountid.eq(account.getId())).where(RecordDao.Properties.Type.eq(type)).list()) {
                records.add(record);
            }
        }

        Collections.sort(records, recordComparator);
        return records;
    }

    public List<Record> getRecordOfExpenseByAccount(Long accountId) {
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        return builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Accountid.eq(accountId)).where(RecordDao.Properties.Expense.eq(true)).list();
    }

    public List<Record> getRecordOfIncomeByAccount(Long accountId) {
        QueryBuilder<Record> builder = daoManager.getDaoSession().queryBuilder(Record.class);
        return builder.orderDesc(RecordDao.Properties.Time).where(RecordDao.Properties.Accountid.eq(accountId)).where(RecordDao.Properties.Expense.eq(false)).list();
    }

    public List<Record> getRecordOfExpenseByBook(Long bookId) {
        AccountUtils util = new AccountUtils(context);
        List<Record> records = new ArrayList<>();
        for (Account account : util.getAllAccounts(bookId)) {
            for (Record record : daoManager.getDaoSession().queryBuilder(Record.class).where(RecordDao.Properties.Accountid.eq(account.getId())).where(RecordDao.Properties.Expense.eq(true)).list()) {
                records.add(record);
            }
        }

        Collections.sort(records, recordComparator);
        return records;
    }

    public List<Record> getRecordOfIncomeByBook(Long bookId) {
        AccountUtils util = new AccountUtils(context);
        List<Record> records = new ArrayList<>();
        for (Account account : util.getAllAccounts(bookId)) {
            for (Record record : daoManager.getDaoSession().queryBuilder(Record.class).where(RecordDao.Properties.Accountid.eq(account.getId())).where(RecordDao.Properties.Expense.eq(false)).list()) {
                records.add(record);
            }
        }

        Collections.sort(records, recordComparator);
        return records;
    }

    public boolean addRecord(Long accountId, Boolean expense, Float amount, String remark, Integer type, Date time) {
        AccountUtils utilAccount = new AccountUtils(context);
        DirtyUtils utilDirty = new DirtyUtils(context);
        boolean flag = true;
        Record record = new Record(null, accountId, expense, amount, remark == null ? "" : remark, type, time, null);
        if (!utilAccount.calculateAccountBalanceByAmount(record.getAccount(), expense, amount)) {
            flag = false;
        }
        if (!insertRecord(record)) {
            flag = false;
        }
        if (!utilDirty.addDirty(record, false)) {
            flag = false;
        }
        return flag;
    }

    public boolean setRecordRemoteId(Long recordId, Long remoteId) {
        Record record = getRecord(recordId);
        record.setRemoteid(remoteId);
        return updateRecord(record);
    }

    public boolean hasRecordRemoteId(Long recordId) {
        return getRecord(recordId).getRemoteid() != null;
    }

    public boolean updateRecord(Long recordId, Long accountId, Boolean expense, Float amount, String remark, Integer type, Date time) {
        AccountUtils utilAccount = new AccountUtils(context);
        DirtyUtils utilDirty = new DirtyUtils(context);
        boolean flag = true;
        Record record = getRecord(recordId);
        Boolean expenseLast = record.getExpense();
        Float amountLast = record.getAmount();
        if (accountId != null) {
            record.setAccountid(accountId);
        }
        if (expense != null) {
            record.setExpense(expense);
            if (amount != null) {
                record.setAmount(amount);
                if (!utilAccount.calculateAccountBalanceByAmount(record.getAccount(), !expenseLast, amountLast)) {
                    flag = false;
                }
                if (!utilAccount.calculateAccountBalanceByAmount(record.getAccount(), expense, amount)) {
                    flag = false;
                }
            } else {
                if (expense != expenseLast) {
                    if (!utilAccount.calculateAccountBalanceByAmount(record.getAccount(), expense, amountLast * 2)) {
                        flag = false;
                    }
                }
            }
        } else {
            if (amount != null) {
                record.setAmount(amount);
                if (!utilAccount.calculateAccountBalanceByAmount(record.getAccount(), expenseLast, amount - amountLast)) {
                    flag = false;
                }
            }
        }
        if (remark != null) {
            record.setRemark(remark);
        }
        if (type != null) {
            record.setType(type);
        }
        if (time != null) {
            record.setTime(time);
        }
        if (!updateRecord(record)) {
            flag = false;
        }
        if (!utilDirty.addDirty(record, false)) {
            flag = false;
        }
        return flag;
    }

    public boolean deleteRecord(Long recordId) {
        AccountUtils utilAccount = new AccountUtils(context);
        DirtyUtils utilDirty = new DirtyUtils(context);
        boolean flag = true;
        Record record = getRecord(recordId);
        if (!utilAccount.calculateAccountBalanceByAmount(record.getAccount(), !record.getExpense(), record.getAmount())) {
            flag = false;
        }
        if (!utilDirty.addDirty(record, true)) {
            flag = false;
        }
        if (!deleteRecord(record)) {
            flag = false;
        }
        return flag;
    }
}