package com.cirnoteam.accountingassistant.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.cirnoteam.accountingassistant.gen.DaoSession;
import com.cirnoteam.accountingassistant.gen.AccountDao;
import com.cirnoteam.accountingassistant.gen.UserDao;
import com.cirnoteam.accountingassistant.gen.BookDao;

/**
 * Created by Yifan on 2017/7/15.
 */
@Entity(nameInDb = "book")
public class Book {
    @Id(autoincrement = true)
    private Long book;
    @NotNull
    private String username;
    @ToOne(joinProperty = "username")
    private User user;
    @NotNull
    private String name;
    @Unique
    private Long remoteid;
    @ToMany(referencedJoinProperty = "bookid")
    private List<Account> accounts;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1097957864)
    private transient BookDao myDao;

    @Generated(hash = 1586663125)
    public Book(Long book, @NotNull String username, @NotNull String name,
                Long remoteid) {
        this.book = book;
        this.username = username;
        this.name = name;
        this.remoteid = remoteid;
    }

    @Generated(hash = 1839243756)
    public Book() {
    }

    public Long getBook() {
        return this.book;
    }

    public void setBook(Long book) {
        this.book = book;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRemoteid() {
        return this.remoteid;
    }

    public void setRemoteid(Long remoteid) {
        this.remoteid = remoteid;
    }

    @Generated(hash = 1867105156)
    private transient String user__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1609607999)
    public User getUser() {
        String __key = this.username;
        if (user__resolvedKey == null || user__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 313590502)
    public void setUser(@NotNull User user) {
        if (user == null) {
            throw new DaoException(
                    "To-one property 'username' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            username = user.getUsername();
            user__resolvedKey = username;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 994086165)
    public List<Account> getAccounts() {
        if (accounts == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccountDao targetDao = daoSession.getAccountDao();
            List<Account> accountsNew = targetDao._queryBook_Accounts(book);
            synchronized (this) {
                if (accounts == null) {
                    accounts = accountsNew;
                }
            }
        }
        return accounts;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 121514453)
    public synchronized void resetAccounts() {
        accounts = null;
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
    @Generated(hash = 1115456930)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookDao() : null;
    }
}
