package com.landleaf.everyday.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.landleaf.everyday.bean.User;

/**
 * Author：Lei on 2020/12/10
 * @Insert 注解声明当前的方法为新增的方法，并且可以设置当新增冲突的时候处理的方法
 * @Delete 注解声明当前的方法是一个删除方法
 * @Update 注解声明当前方法是一个更新方法
 * Room的查很接近原生的SQL语句。@Query注解不仅可以声明这是一个查询语句，也可以用来删除和修改，不可以用来新增。
 */

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Delete
    void deleteUser(User user);

    @Update
    void updateUser(User user);

    // 查询一个
    @Query("SELECT * FROM user WHERE id=:id")
    User findShoeById(Long id);
}
