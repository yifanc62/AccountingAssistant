package com.cirnoteam.accountingassistant.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.cirnoteam.accountingassistant.gen.DaoSession;
import com.cirnoteam.accountingassistant.gen.AccountDao;
import com.cirnoteam.accountingassistant.gen.RecordDao;

/**
 * Created by Yifan on 2017/7/15.
 */

@Entity(nameInDb = "record")
public class Record {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private Long accountid;
    @ToOne(joinProperty = "accountid")
    private Account account;
    @NotNull
    private Boolean expense;
    @NotNull
    private Float amount;
    private String remark;
    @NotNull
    private Integer type;
    @NotNull
    private Date time;
    @Unique
    private Long remoteid;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 765166123)
    private transient RecordDao myDao;

    @Generated(hash = 10205916)
    public Record(Long id, @NotNull Long accountid, @NotNull Boolean expense,
                  @NotNull Float amount, String remark, @NotNull Integer type,
                  @NotNull Date time, Long remoteid) {
        this.id = id;
        this.accountid = accountid;
        this.expense = expense;
        this.amount = amount;
        this.remark = remark;
        this.type = type;
        this.time = time;
        this.remoteid = remoteid;
    }

    @Generated(hash = 477726293)
    public Record() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountid() {
        return this.accountid;
    }

    public void setAccountid(Long accountid) {
        this.accountid = accountid;
    }

    public Boolean getExpense() {
        return this.expense;
    }

    public void setExpense(Boolean expense) {
        this.expense = expense;
    }

    public Float getAmount() {
        return this.amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getRemoteid() {
        return this.remoteid;
    }

    public void setRemoteid(Long remoteid) {
        this.remoteid = remoteid;
    }

    @Generated(hash = 1501133588)
    private transient Long account__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1970307578)
    public Account getAccount() {
        Long __key = this.accountid;
        if (account__resolvedKey == null || !account__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccountDao targetDao = daoSession.getAccountDao();
            Account accountNew = targetDao.load(__key);
            synchronized (this) {
                account = accountNew;
                account__resolvedKey = __key;
            }
        }
        return account;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 327266077)
    public void setAccount(@NotNull Account account) {
        if (account == null) {
            throw new DaoException(
                    "To-one property 'accountid' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.account = account;
            accountid = account.getId();
            account__resolvedKey = accountid;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1505145191)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecordDao() : null;
    }
}
