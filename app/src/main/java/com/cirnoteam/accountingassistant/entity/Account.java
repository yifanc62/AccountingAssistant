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
import com.cirnoteam.accountingassistant.gen.RecordDao;
import com.cirnoteam.accountingassistant.gen.BookDao;
import com.cirnoteam.accountingassistant.gen.AccountDao;

/**
 * Created by Yifan on 2017/7/15.
 */
@Entity(nameInDb = "account")
public class Account {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private Long bookid;
    @ToOne(joinProperty = "bookid")
    private Book book;
    @NotNull
    private Integer type;
    private String name;
    @Unique
    private Long remoteid;
    @ToMany(referencedJoinProperty = "accountid")
    private List<Record> records;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 335469827)
    private transient AccountDao myDao;

    @Generated(hash = 28241216)
    public Account(Long id, @NotNull Long bookid, @NotNull Integer type,
                   String name, Long remoteid) {
        this.id = id;
        this.bookid = bookid;
        this.type = type;
        this.name = name;
        this.remoteid = remoteid;
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookid() {
        return this.bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    @Generated(hash = 893611298)
    private transient Long book__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1712648062)
    public Book getBook() {
        Long __key = this.bookid;
        if (book__resolvedKey == null || !book__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookDao targetDao = daoSession.getBookDao();
            Book bookNew = targetDao.load(__key);
            synchronized (this) {
                book = bookNew;
                book__resolvedKey = __key;
            }
        }
        return book;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1014915584)
    public void setBook(@NotNull Book book) {
        if (book == null) {
            throw new DaoException(
                    "To-one property 'bookid' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.book = book;
            bookid = book.getBook();
            book__resolvedKey = bookid;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1866497864)
    public List<Record> getRecords() {
        if (records == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RecordDao targetDao = daoSession.getRecordDao();
            List<Record> recordsNew = targetDao._queryAccount_Records(id);
            synchronized (this) {
                if (records == null) {
                    records = recordsNew;
                }
            }
        }
        return records;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 3024737)
    public synchronized void resetRecords() {
        records = null;
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
    @Generated(hash = 1812283172)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAccountDao() : null;
    }
}
