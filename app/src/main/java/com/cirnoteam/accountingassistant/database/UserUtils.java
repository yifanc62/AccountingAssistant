package com.cirnoteam.accountingassistant.database;

import android.content.Context;
import android.util.Log;

import com.cirnoteam.accountingassistant.entity.User;
import com.cirnoteam.accountingassistant.gen.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import static com.cirnoteam.accountingassistant.database.DaoManager.TAG;

/**
 * UserUtils
 * 用户操作类
 *
 * @author Yifan
 * @version 0.8
 */

public class UserUtils {
    private DaoManager daoManager;

    /**
     * 构造方法
     *
     * @param context 当前App上下文
     */
    public UserUtils(Context context) {
        daoManager = DaoManager.getInstance();
        daoManager.initManager(context);
    }

    /**
     * 插入user
     *
     * @param user 要插入的用户对象，对象必须初始化
     * @return 成功/失败flag
     */
    public boolean insertUser(User user) {
        return daoManager.getDaoSession().insert(user) != -1;
    }

    /**
     * 批量插入user
     *
     * @param users 要插入的用户对象列表，所有对象必须初始化
     * @return 成功/失败flag
     */
    public boolean insertUsers(final List<User> users) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (User user : users) {
                        daoManager.getDaoSession().insertOrReplace(user);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改user
     *
     * @param user 要修改的用户对象，必须具有username
     * @return 成功/失败flag
     */
    public boolean updateUser(User user) {
        boolean flag = false;
        try {
            daoManager.getDaoSession().update(user);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除user
     *
     * @param user 要删除的用户对象，必须具有username
     * @return 成功/失败flag
     */
    public boolean deleteUser(User user) {
        boolean flag = false;
        try {
            //删除指定ID
            daoManager.getDaoSession().delete(user);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //daoManager.getDaoSession().deleteAll(); //删除所有记录
        return flag;
    }

    /**
     * 查询单条
     *
     * @param key 主键值
     * @return 用户对象
     */
    public User listOneStudent(long key) {
        return daoManager.getDaoSession().load(User.class, key);
    }

    /**
     * 全部查询
     *
     * @return 所有用户对象
     */
    public List<User> listAll() {
        return daoManager.getDaoSession().loadAll(User.class);
    }

    /**
     * 原生查询
     */
    public void queryNative() {
        //查询条件
        String where = "where name like ? and _id > ?";
        //使用sql进行查询
        List<User> list = daoManager.getDaoSession().queryRaw(User.class, where, "%l%", "6");
        Log.i(TAG, list + "");
    }

    /**
     * QueryBuilder查询
     */
    public void queryBuilder() {
        //查询构建器
        QueryBuilder<User> queryBuilder = daoManager.getDaoSession().queryBuilder(User.class);
        //查询年龄大于19的北京
        List<User> list = queryBuilder.where(UserDao.Properties.Current.eq(true)).list();
        Log.i(TAG, list + "");
    }
}
