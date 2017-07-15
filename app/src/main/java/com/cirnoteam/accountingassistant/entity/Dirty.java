package com.cirnoteam.accountingassistant.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;

import java.sql.Timestamp;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.cirnoteam.accountingassistant.gen.DaoSession;
import com.cirnoteam.accountingassistant.gen.UserDao;
import com.cirnoteam.accountingassistant.gen.DirtyDao;

/**
 * Created by Yifan on 2017/7/15.
 */
@Entity(nameInDb = "dirty")
public class Dirty {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String username;
    @ToOne(joinProperty = "username")
    private User user;
    @NotNull
    private Long rid;
    @NotNull
    private Integer type;
    private Long remoteid;
    private Boolean deleted;
    @NotNull
    private Date time;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1247244084)
    private transient DirtyDao myDao;

    @Generated(hash = 387672267)
    public Dirty(Long id, @NotNull String username, @NotNull Long rid,
                 @NotNull Integer type, Long remoteid, Boolean deleted,
                 @NotNull Date time) {
        this.id = id;
        this.username = username;
        this.rid = rid;
        this.type = type;
        this.remoteid = remoteid;
        this.deleted = deleted;
        this.time = time;
    }

    @Generated(hash = 1243795482)
    public Dirty() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRid() {
        return this.rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getRemoteid() {
        return this.remoteid;
    }

    public void setRemoteid(Long remoteid) {
        this.remoteid = remoteid;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
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
    @Generated(hash = 832342260)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDirtyDao() : null;
    }
}
