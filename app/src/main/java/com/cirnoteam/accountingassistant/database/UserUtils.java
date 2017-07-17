package com.cirnoteam.accountingassistant.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.cirnoteam.accountingassistant.entity.User;
import com.cirnoteam.accountingassistant.gen.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * UserUtils
 * 用户操作类
 *
 * @author Yifan
 * @version 1.0
 */

public class UserUtils {
    private DaoManager daoManager;

    public UserUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
    }

    public boolean insertUser(User user) {
        return daoManager.getDaoSession().insert(user) != -1;
    }

    private boolean updateUser(User user) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().update(user);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean deleteUser(User user) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().delete(user);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public User getUser(String username) {
        return daoManager.getDaoSession().load(User.class, username);
    }

    public List<User> getAllUsers() {
        return daoManager.getDaoSession().loadAll(User.class);
    }

    public User getCurrentUser() {
        QueryBuilder<User> builder = daoManager.getDaoSession().queryBuilder(User.class);
        return builder.where(UserDao.Properties.Current.eq(true)).unique();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public String getCurrentPassword() {
        return getCurrentUser().getPassword();
    }

    public String getCurrentToken() {
        return getCurrentUser().getToken();
    }

    public String getCurrentDeviceName() {
        return getCurrentUser().getDevicename();
    }

    public String getCurrentDeviceUuid() {
        return getCurrentUser().getUuid();
    }

    public boolean hasCurrentAvatar() {
        return getCurrentUser().getAvatarPath() != null;
    }

    public Date getCurrentLastSyncTime() {
        return getCurrentUser().getLastsynctime();
    }

    public boolean updateCurrentPassword(String newPassword) {
        User user = getCurrentUser();
        user.setPassword(newPassword);
        return updateUser(user);
    }

    public boolean refreshCurrentToken(String newToken) {
        User user = getCurrentUser();
        user.setToken(newToken);
        return updateUser(user);
    }

    public boolean updateDeviceName(String newName) {
        User user = getCurrentUser();
        user.setDevicename(newName);
        return updateUser(user);
    }

    public boolean setAvatar(String avatarPath) {
        User user = getCurrentUser();
        user.setAvatarPath(avatarPath);
        return updateUser(user);
    }

    public boolean register(String username, String password, String token) {
        if (isUserNameLocalDuplicated(username)) {
            Log.e("register", "username duplicated");
            return false;
        }
        User user = new User(username, password, token, getDefaultDeviceName(), generateUuid(), null, true, new Date());
        return insertUser(user);
    }

    public boolean isUserNameLocalDuplicated(String username) {
        QueryBuilder<User> builder = daoManager.getDaoSession().queryBuilder(User.class);
        User user = builder.where(UserDao.Properties.Username.eq(username)).unique();
        return user != null;
    }

    private String getDefaultDeviceName() {
        return Build.MODEL;
    }

    private String generateUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
    }

    public boolean login(String username) {
        QueryBuilder<User> builder = daoManager.getDaoSession().queryBuilder(User.class);
        User user = builder.where(UserDao.Properties.Username.eq(username)).unique();
        user.setCurrent(true);
        return updateUser(user);
    }

    public boolean logout(String username) {
        QueryBuilder<User> builder = daoManager.getDaoSession().queryBuilder(User.class);
        User user = builder.where(UserDao.Properties.Username.eq(username)).unique();
        user.setCurrent(false);
        return updateUser(user);
    }
}
