package com.cirnoteam.accountingassistant.database;

import android.content.Context;

import com.cirnoteam.accountingassistant.entity.Account;
import com.cirnoteam.accountingassistant.gen.AccountDao;
import com.cirnoteam.accountingassistant.json.SyncAccount;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 * BookUtils
 * 账户操作类
 *
 * @author Yifan
 * @version 1.2
 */

public class AccountUtils {
    private DaoManager daoManager;
    private Context context;

    public static final int TYPE_现金 = 0;
    public static final int TYPE_银行卡 = 1;
    public static final int TYPE_支付宝余额 = 2;
    public static final int TYPE_QQ钱包余额 = 3;
    public static final int TYPE_微信余额 = 4;
    public static final int TYPE_余额宝 = 5;
    public static final int TYPE_交通卡 = 6;
    public static final int TYPE_储值卡 = 7;
    public static final int TYPE_校园卡 = 8;
    public static final int TYPE_其他账户 = 9;

    public AccountUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
        this.context = context;
    }

    private boolean insertAccount(Account account) {
        return daoManager.getDaoSession().insert(account) != -1;
    }

    private boolean updateAccount(Account account) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().update(account);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean deleteAccount(Account account) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().delete(account);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public String getDefaultAccountName(Integer type) {
        switch (type) {
            case 0:
                return "现金";
            case 1:
                return "银行卡";
            case 2:
                return "支付宝余额";
            case 3:
                return "QQ钱包余额";
            case 4:
                return "微信余额";
            case 5:
                return "余额宝";
            case 6:
                return "交通卡";
            case 7:
                return "储值卡";
            case 8:
                return "校园卡";
            default:
                return "其他账户";
        }
    }

    public Account getAccount(Long accountId) {
        QueryBuilder<Account> builder = daoManager.getDaoSession().queryBuilder(Account.class);
        return builder.where(AccountDao.Properties.Id.eq(accountId)).unique();
    }

    public List<Account> getAllAccounts(Long bookId) {
        QueryBuilder<Account> builder = daoManager.getDaoSession().queryBuilder(Account.class);
        return builder.where(AccountDao.Properties.Bookid.eq(bookId)).list();
    }

    public boolean addAccount(Long bookId, Integer type, Float balance, String name) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        if (name.equals("")) {
            name = null;
        }
        Account account = new Account(null, bookId, type, balance == null ? 0 : balance, name == null ? getDefaultAccountName(type) : (name.equals("") ? getDefaultAccountName(type) : name), null);
        if (!insertAccount(account)) {
            flag = false;
        }
        if (!util.addDirty(account, false)) {
            flag = false;
        }
        return flag;
    }

    public boolean setAccountRemoteId(Long accountId, Long remoteId) {
        Account account = getAccount(accountId);
        account.setRemoteid(remoteId);
        return updateAccount(account);
    }

    public boolean hasAccountRemoteId(Long accountId) {
        return getAccount(accountId).getRemoteid() != null;
    }

    public boolean updateAccountType(Long accountId, Integer type) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Account account = getAccount(accountId);
        account.setType(type);
        if (!util.addDirty(account, false)) {
            flag = false;
        }
        if (!updateAccount(account)) {
            flag = false;
        }
        return flag;
    }

    public boolean updateAccountName(Long accountId, String newName) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Account account = getAccount(accountId);
        account.setName(newName);
        if (!util.addDirty(account, false)) {
            flag = false;
        }
        if (!updateAccount(account)) {
            flag = false;
        }
        return flag;
    }

    public boolean updateAccountBalance(Long accountId, Float newBalance) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Account account = getAccount(accountId);
        account.setBalance(newBalance);
        if (!util.addDirty(account, false)) {
            flag = false;
        }
        if (!updateAccount(account)) {
            flag = false;
        }
        return flag;
    }

    public boolean calculateAccountBalanceByAmount(Long accountId, Boolean expense, Float amount) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Account account = getAccount(accountId);
        Float balance = account.getBalance();
        if (expense) {
            balance -= amount;
        } else {
            balance += amount;
        }
        account.setBalance(balance);
        if (!util.addDirty(account, false)) {
            flag = false;
        }
        if (!updateAccount(account)) {
            flag = false;
        }
        return flag;
    }

    public boolean hasAccountName(Long accountId) {
        return getAccount(accountId).getName() != null;
    }

    public boolean deleteAccount(Long accountId) {
        DirtyUtils util = new DirtyUtils(context);
        boolean flag = true;
        Account account = getAccount(accountId);
        if (!util.addDirty(account, true)) {
            flag = false;
        }
        if (!deleteAccount(account)) {
            flag = false;
        }
        return flag;
    }

    /*
    查询账本的所有账户
     */
    public List<Account> getAccountsOfBook(Long bookid) {
        QueryBuilder<Account> queryBuilder = daoManager.getDaoSession().queryBuilder(Account.class);
        return queryBuilder.where(AccountDao.Properties.Bookid.eq(bookid)).list();
    }


//    public boolean addSyncAccount(SyncAccount syncAccount){
//        Account account = new Account(syncAccount.getId(),,syncAccount.getBalance(),);
//        Account account = new Account()
//    }

}
