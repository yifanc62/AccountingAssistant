package com.cirnoteam.accountingassistant.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.cirnoteam.accountingassistant.gen.DaoSession;
import com.cirnoteam.accountingassistant.gen.DirtyDao;
import com.cirnoteam.accountingassistant.gen.BookDao;
import com.cirnoteam.accountingassistant.gen.UserDao;

/**
 * Created by Yifan on 2017/7/15.
 */

@Entity(nameInDb = "user")
public class User {
    @Id
    @NotNull
    @Unique
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String token;
    @NotNull
    private String devicename;
    @NotNull
    private String uuid;
    @Property(nameInDb = "avatar")
    private String avatarPath;
    @NotNull
    private Boolean current;
    @NotNull
    private Date lastsynctime;
    @ToMany(referencedJoinProperty = "username")
    private List<Book> books;
    @ToMany(referencedJoinProperty = "username")
    private List<Dirty> dirties;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 694309216)
    public User(@NotNull String username, @NotNull String password,
                @NotNull String token, @NotNull String devicename, @NotNull String uuid,
                String avatarPath, @NotNull Boolean current,
                @NotNull Date lastsynctime) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.devicename = devicename;
        this.uuid = uuid;
        this.avatarPath = avatarPath;
        this.current = current;
        this.lastsynctime = lastsynctime;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevicename() {
        return this.devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAvatarPath() {
        return this.avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Boolean getCurrent() {
        return this.current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Date getLastsynctime() {
        return this.lastsynctime;
    }

    public void setLastsynctime(Date lastsynctime) {
        this.lastsynctime = lastsynctime;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1836274551)
    public List<Book> getBooks() {
        if (books == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookDao targetDao = daoSession.getBookDao();
            List<Book> booksNew = targetDao._queryUser_Books(username);
            synchronized (this) {
                if (books == null) {
                    books = booksNew;
                }
            }
        }
        return books;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 353255226)
    public synchronized void resetBooks() {
        books = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 952149131)
    public List<Dirty> getDirties() {
        if (dirties == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DirtyDao targetDao = daoSession.getDirtyDao();
            List<Dirty> dirtiesNew = targetDao._queryUser_Dirties(username);
            synchronized (this) {
                if (dirties == null) {
                    dirties = dirtiesNew;
                }
            }
        }
        return dirties;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 279493214)
    public synchronized void resetDirties() {
        dirties = null;
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
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }
}
